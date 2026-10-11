# Moja online vizitka

Osobny web RH vytvoreny pomocou HTML, CSS a JavaScriptu.

## Mapa a pocasie

- `pages/map.html`: FEI STU, fiktivny domov v Karlovej Vsi, pridavanie a vyber miest.
- Vzdialenost pocita vlastna funkcia `calculateDistance` pomocou Haversinovho vzorca v kilometroch.
- Body sa ukladaju do localStorage pod klucom `rh-map-points-v1`.
- Suradnice skoly a ukazkoveho domova su na zaciatku `js/map.js`.
- Mapa a pocasie vyzaduju internetove pripojenie. Projekt je vhodne otvorit cez lokalny HTTP server, napriklad Live Server.

## Lokalne pismo

- Web pouziva **Poppins**, lokalne nacitany cez `@font-face` v `css/styles.css`. Pri zobrazeni stranky sa font nestahuje z externej sluzby.
- Subory `.woff2` v `fonts/` obsahuju bezny rez (400), tucny rez (700) a kurzivu (400), vzdy vo variantoch Latin a Latin Extended pre slovensku diakritiku.
- Autor: Copyright 2020 The Poppins Project Authors (https://github.com/itfoundry/Poppins).
- Licencia: **SIL Open Font License 1.1**. Plne znenie je prilozene v [fonts/OFL-Poppins.txt](fonts/OFL-Poppins.txt).
- Zdroj WOFF2 suborov: balicek `@fontsource/poppins`, verzia `5.2.7` (https://www.npmjs.com/package/@fontsource/poppins/v/5.2.7). Odkazy v tejto dokumentacii sluzia iba na uvedenie zdroja.

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
