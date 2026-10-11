const SCHOOL = { name: 'FEI STU Bratislava', lat: 48.151965, lng: 17.072995 };
const HOME = { name: 'Môj domov (fiktívna poloha)', lat: 48.1605, lng: 17.0525 };
const POINTS_STORAGE_KEY = 'rh-map-points-v1';

function calculateDistance(first, second) {
    const radians = Math.PI / 180;
    const latitudeDifference = (second.lat - first.lat) * radians;
    const longitudeDifference = (second.lng - first.lng) * radians;
    const a = Math.sin(latitudeDifference / 2) ** 2 +
        Math.cos(first.lat * radians) * Math.cos(second.lat * radians) *
        Math.sin(longitudeDifference / 2) ** 2;
    const boundedA = Math.max(0, Math.min(1, a));
    return 6371 * 2 * Math.atan2(Math.sqrt(boundedA), Math.sqrt(1 - boundedA));
}

function createPopup(name, description) {
    const container = document.createElement('div');
    const title = document.createElement('strong');
    title.className = 'map-popup-title';
    title.textContent = name;
    container.append(title, document.createTextNode(description));
    return container;
}

async function loadWeather() {
    const weather = document.querySelector('#weather');
    const retry = document.querySelector('#weather-retry');
    weather.textContent = 'Načítavam teplotu a rýchlosť vetra…';
    retry.hidden = true;
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 12000);

    try {
        const url = 'https://api.open-meteo.com/v1/forecast' +
            `?latitude=${SCHOOL.lat}&longitude=${SCHOOL.lng}` +
            '&current=temperature_2m,wind_speed_10m&timezone=Europe%2FBratislava';
        const response = await fetch(url, { signal: controller.signal });
        if (!response.ok) throw new Error('Weather request failed.');
        const data = await response.json();
        const current = data.current;
        if (!current || !Number.isFinite(current.temperature_2m) ||
            !Number.isFinite(current.wind_speed_10m)) {
            throw new Error('Invalid weather response.');
        }

        weather.textContent = `Teplota: ${current.temperature_2m.toLocaleString('sk-SK')} °C. ` +
            `Vietor: ${current.wind_speed_10m.toLocaleString('sk-SK')} km/h.`;
    } catch (error) {
        weather.textContent = 'Počasie sa nepodarilo načítať. Skontrolujte pripojenie a skúste to znova.';
        retry.hidden = false;
    } finally {
        clearTimeout(timeout);
    }
}

function isValidPoint(point) {
    return point && typeof point.id === 'string' && point.id.length > 0 &&
        typeof point.name === 'string' && point.name.trim().length > 0 && point.name.length <= 60 &&
        Number.isFinite(point.lat) && point.lat >= -90 && point.lat <= 90 &&
        Number.isFinite(point.lng) && point.lng >= -180 && point.lng <= 180;
}

function initializeMap() {
    const status = document.querySelector('#map-status');
    if (typeof L === 'undefined') {
        status.textContent = 'Mapu sa nepodarilo načítať. Skontrolujte internetové pripojenie a obnovte stránku.';
        return;
    }

    const map = L.map('map').setView([SCHOOL.lat, SCHOOL.lng], 14);
    const tiles = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);
    tiles.on('tileerror', () => {
        status.textContent = 'Niektoré mapové podklady sa nenačítali. Skontrolujte pripojenie; uložené body a výpočet vzdialenosti môžete používať ďalej.';
    });

    function createIcon(type, label) {
        return L.divIcon({
            className: `map-marker map-marker-${type}`,
            html: `<span aria-hidden="true">${label}</span>`,
            iconSize: [32, 32], iconAnchor: [16, 16]
        });
    }

    const schoolMarker = L.marker([SCHOOL.lat, SCHOOL.lng], {
        icon: createIcon('school', 'Š'), title: SCHOOL.name, alt: SCHOOL.name
    }).addTo(map).bindPopup(createPopup(SCHOOL.name, 'Moja škola.'));
    const homeMarker = L.marker([HOME.lat, HOME.lng], {
        icon: createIcon('home', 'D'), title: HOME.name, alt: HOME.name
    }).addTo(map).bindPopup(createPopup(HOME.name, 'Ukážkové bydlisko v Karlovej Vsi.'));
    const pointMarkers = new Map();
    const pointSelect = document.querySelector('#point-select');
    const targetSelect = document.querySelector('#target-select');
    const distanceResult = document.querySelector('#distance-result');
    const storageStatus = document.querySelector('#storage-status');
    const form = document.querySelector('#point-form');
    const nameInput = document.querySelector('#point-name');
    const latitudeInput = document.querySelector('#point-latitude');
    const longitudeInput = document.querySelector('#point-longitude');
    let points = [];
    let connection = null;
    let preview = null;

    try {
        const stored = JSON.parse(localStorage.getItem(POINTS_STORAGE_KEY) || '[]');
        if (!Array.isArray(stored) || !stored.every(isValidPoint) ||
            new Set(stored.map(point => point.id)).size !== stored.length) {
            throw new Error('Invalid saved points.');
        }
        points = stored;
    } catch (error) {
        storageStatus.textContent = 'Uložené body sa nepodarilo načítať. Môžete pridať nové miesta.';
    }

    function savePoints() {
        try {
            localStorage.setItem(POINTS_STORAGE_KEY, JSON.stringify(points));
            storageStatus.textContent = 'Miesta sú uložené v tomto prehliadači a zostanú tu aj po obnovení stránky.';
        } catch (error) {
            storageStatus.textContent = 'Prehliadač nepovolil uloženie. Body fungujú, ale po obnovení stránky sa tieto zmeny môžu stratiť.';
        }
    }

    function updateDistance() {
        if (connection) {
            map.removeLayer(connection);
            connection = null;
        }
        schoolMarker.setPopupContent(createPopup(SCHOOL.name, 'Moja škola.'));
        homeMarker.setPopupContent(createPopup(HOME.name, 'Ukážkové bydlisko v Karlovej Vsi.'));
        for (const point of points) {
            pointMarkers.get(point.id).setPopupContent(createPopup(point.name, 'Moje uložené miesto.'));
        }
        const point = points.find(item => item.id === pointSelect.value);
        if (!point) {
            distanceResult.textContent = 'Zatiaľ nemáte uložené žiadne body. Pridajte prvé miesto kliknutím na mapu alebo zadaním súradníc.';
            return;
        }

        const target = targetSelect.value === 'home' ? HOME : SCHOOL;
        const targetMarker = targetSelect.value === 'home' ? homeMarker : schoolMarker;
        const distance = calculateDistance(point, target).toLocaleString('sk-SK', {
            minimumFractionDigits: 2, maximumFractionDigits: 2
        });
        const message = `${point.name} → ${target.name}: ${distance} km vzdušnou čiarou.`;
        distanceResult.textContent = message;
        const color = getComputedStyle(document.documentElement).getPropertyValue('--color-accent').trim();
        connection = L.polyline([[point.lat, point.lng], [target.lat, target.lng]], {
            color, weight: 3, dashArray: '8 6'
        }).addTo(map);
        pointMarkers.get(point.id).setPopupContent(createPopup(point.name, message));
        targetMarker.setPopupContent(createPopup(target.name, message));
        map.fitBounds(connection.getBounds(), { padding: [35, 35], maxZoom: 16 });
    }

    function addMarker(point) {
        const marker = L.marker([point.lat, point.lng], {
            icon: createIcon('point', 'B'), title: point.name, alt: point.name
        }).addTo(map).bindPopup(createPopup(point.name, 'Moje uložené miesto.'));
        marker.on('click', () => {
            pointSelect.value = point.id;
            updateDistance();
            marker.openPopup();
        });
        pointMarkers.set(point.id, marker);
    }

    function updatePointList(selectedId) {
        pointSelect.replaceChildren();
        document.querySelector('#distance-fields').disabled = points.length === 0;
        if (points.length === 0) {
            pointSelect.add(new Option('Najprv pridajte bod', ''));
        } else {
            for (const point of points) {
                pointSelect.add(new Option(point.name, point.id));
            }
            if (points.some(point => point.id === selectedId)) pointSelect.value = selectedId;
        }
        updateDistance();
    }

    map.on('click', (event) => {
        const position = event.latlng.wrap();
        latitudeInput.value = position.lat.toFixed(6);
        longitudeInput.value = position.lng.toFixed(6);
        if (preview) map.removeLayer(preview);
        preview = L.circleMarker(position, { radius: 8 }).addTo(map);
        status.textContent = 'Miesto je vybrané. Zadajte jeho názov a stlačte „Pridať bod“.';
        nameInput.focus({ preventScroll: true });
    });

    nameInput.addEventListener('input', () => nameInput.setCustomValidity(''));
    form.addEventListener('submit', (event) => {
        event.preventDefault();
        if (!nameInput.value.trim()) {
            nameInput.setCustomValidity('Zadajte názov miesta, nie iba medzery.');
            nameInput.reportValidity();
            return;
        }
        const point = {
            id: `${Date.now()}-${Math.random().toString(36).slice(2)}`,
            name: nameInput.value.trim(), lat: Number(latitudeInput.value), lng: Number(longitudeInput.value)
        };
        if (!form.reportValidity() || !isValidPoint(point)) return;
        points.push(point);
        addMarker(point);
        savePoints();
        updatePointList(point.id);
        if (preview) {
            map.removeLayer(preview);
            preview = null;
        }
        form.reset();
        status.textContent = `Bod „${point.name}“ je pridaný. Môžete pridať ďalší alebo zmeniť cieľ.`;
    });

    pointSelect.addEventListener('change', updateDistance);
    targetSelect.addEventListener('change', updateDistance);
    document.querySelector('#delete-point').addEventListener('click', () => {
        const id = pointSelect.value;
        if (!pointMarkers.has(id)) return;
        map.removeLayer(pointMarkers.get(id));
        pointMarkers.delete(id);
        points = points.filter(point => point.id !== id);
        savePoints();
        updatePointList();
        status.textContent = 'Vybraný bod bol odstránený.';
    });

    for (const point of points) addMarker(point);
    document.querySelector('#point-fields').disabled = false;
    updatePointList();
    status.textContent = 'Š označuje školu, D domov a B pridaný bod. Kliknutím na mapu vyberiete nové miesto.';
}

document.querySelector('#weather-retry').addEventListener('click', loadWeather);
loadWeather();
initializeMap();
