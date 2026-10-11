# Moja online vizitka

Osobny web RH vytvoreny pomocou HTML, CSS a JavaScriptu.

## Mapa a pocasie

- `pages/map.html`: FEI STU, fiktivny domov v Karlovej Vsi, pridavanie a vyber miest.
- Vzdialenost pocita vlastna funkcia `calculateDistance` pomocou Haversinovho vzorca v kilometroch.
- Body sa ukladaju do localStorage pod klucom `rh-map-points-v1`.
- Suradnice skoly a ukazkoveho domova su na zaciatku `js/map.js`.
- Mapa a pocasie vyzaduju internetove pripojenie. Projekt je vhodne otvorit cez lokalny HTTP server, napriklad Live Server.

## Zdroje

- `img/workspace.jpg`: fotografia vlastneho pracovneho stola dodana autorom webu (povodny subor `img/Moj stol.jpg`). Fotografia nebola generovana ani upravovana pomocou AI.

## Interaktivny obrazok

- `pages/workspace.html` obsahuje pat hotspotov; ovladanie pouziva iba CSS `:hover` a `:focus-within`, funguje aj bez JavaScriptu.
- Body maju percentualne suradnice voci obrazku. Do sirky 768 px je vzdy jeden panel pod obrazkom; prvy sa zobrazi automaticky a hover ma prednost pred fokusom.
- Panely na mobile maju pevnu vysku a pri dlhsom texte vnutorne rolovanie, aby obsah pod obrazkom neposkakoval.

## Dalsie zdroje

- Navod z cvicenia: Weather API a Leaflet mapa.
- [Leaflet 1.9.4](https://leafletjs.com/), licencia BSD-2-Clause; nacitany z unpkg CDN iba na stranke mapy.
- Mapove podklady: [OpenStreetMap contributors](https://www.openstreetmap.org/copyright), data pod licenciou ODbL.
- Pocasie: [Open-Meteo](https://open-meteo.com/), data pod licenciou CC BY 4.0.

Pri tvorbe HTML, CSS a JavaScriptu som pouzil OpenAI Codex na generovanie a vysvetlenie kodu.
