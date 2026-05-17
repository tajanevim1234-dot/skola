# Skuska trening - OOP mix

Toto je cvicne zadanie poskladane zo stylu tvojich starsich skusok.
Ries vsetko v baliku `oop.trening`. Testy su v priecinku `test`.

Spustenie:

```bash
mvn test
```

## Cast A - Rezervacny system

Vytvor hierarchiu rezervacii:

- `Reservation` je abstraktna trieda.
- Ma private atributy `code`, `customerName`, `price`.
- Implementuje `Comparable<Reservation>`.
- Prirodzene poradie je podla `code` abecedne.
- `equals` a `hashCode` porovnavaju rezervacie podla `code`.
- Konstruktor vyhodi `IllegalArgumentException`, ak je cena zaporna alebo nulova.

Triedy:

- `HotelReservation extends Reservation`
  - private atribut `nights`
  - konstruktor `HotelReservation(String code, String customerName, double price, int nights)`
  - getter `getNights()`

- `FlightReservation extends Reservation implements Upgradable`
  - private atribut `seatClass`
  - konstruktor `FlightReservation(String code, String customerName, double price, String seatClass)`
  - getter `getSeatClass()`
  - metoda `upgrade()`
    - `economy -> business`, cena +150
    - `business -> first`, cena +300
    - `first` sa uz nemeni

Vynimky:

- `DuplicateReservationException extends Exception`
- `ReservationNotFoundException extends Exception`

`ReservationSystem`:

- ma prave jednu mapu `Map<String, Reservation>`
- `addReservation(Reservation r)`
- `removeReservation(String code)`
- `getReservation(String code)`
- `getReservationCount()`
- `getReservationsSortedByCode()`
- `findReservationsByPriceRange(double min, double max)`
- `calculateTotalPrice()`
- genericka metoda `<T extends Reservation> List<T> getReservationsByType(Class<T> type)`

## Cast B - Export cez Strategy

Rozhranie `ReservationExporter`:

- `String export(Reservation reservation)`
- `String export(Collection<Reservation> reservations)`

Implementacie:

- `CsvReservationExporter`
- `TextReservationExporter`

CSV format:

```text
code; customer; price
R01; Adam; 100.0
```

Pri exporte kolekcie CSV zacina hlavickou `code; customer; price`.
Pri exporte jednej rezervacie CSV hlavicku nema.

Text format:

```text
[R01] Adam - 100.0
```

`ReservationSystem` ma mat nastavitelny exporter:

- `setExporter(ReservationExporter exporter)`
- `exportReservations()`

## Cast C - Builder a Director

`Invoice`:

- private atributy `customer`, `content`, `total`
- package-private konstruktor, teda bez `public`, `protected`, `private`
- gettery

`InvoiceBuilderInterface`:

- `setCustomer(String customer)`
- `setContent(String content)`
- `setTotal(double total)`
- `reset()`
- `Invoice build() throws InvoiceNotBuildableException`

`InvoiceBuilder`:

- ma len private atributy
- `build()` vyhodi `InvoiceNotBuildableException`, ak chyba customer alebo content, alebo total nie je kladne cislo
- po uspesnom `build()` sa builder resetuje

`TripInvoiceDirector`:

- ma private atribut typu `InvoiceBuilderInterface`
- konstruktor `TripInvoiceDirector(InvoiceBuilderInterface builder)`
- metoda `createReservationInvoice(Reservation reservation)`
- metoda `createUpgradeInvoice(FlightReservation reservation)`
- director len nastavi builder, samotne `build()` vola test

## Cast D - Factory a Decorator

`Drone`:

- `void move()`
- `int getPosition()`
- `String getName()`

`DroneFactory`:

- `Drone createLightDrone()`
- `Drone createHeavyDrone()`

`ExamFactory`:

- `static DroneFactory createCityDroneFactory()`
- `static DroneFactory createMountainDroneFactory()`
- `static Drone createTurbo(Drone drone)`
- `static Drone createShield(Drone drone)`

Pohyb:

- city light: +2
- city heavy: +1
- mountain light: +3
- mountain heavy: +2
- turbo decorator zdvojnasobi dalsi krok
- shield decorator prida k dalsiemu kroku +1
- decoratory musia zachovat aktualnu poziciu obaleneho drona

## Rada

Najprv sprav, aby projekt kompiloval. Potom ries testy postupne zhora nadol.
