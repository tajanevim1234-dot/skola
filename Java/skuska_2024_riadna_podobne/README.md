# Skuska 2024 riadna - podobny trening

Toto je cvicne zadanie podobne stylom ako `oop2024_skuska_riadna`.
Namiesto telefonu budes robit jednoduche inteligentne hodinky.

Balik:

```text
oop.podobne.watch
```

Spustenie testov:

```bash
mvn test
```

## Co mas vytvorit

Mas triedu `SmartWatch`, aplikacie `WatchApplication` a stavy hodiniek.
Pouzi navrhovy vzor **State**.

Hodinky maju 4 stavy:

- `Off`
- `WaitingForPin`
- `Menu`
- `ActivityRunning`

PIN je:

```text
1111
```

## Aplikacie

Trieda `WatchApplication` ma:

- private `name`
- private `canInstallApplication`
- private `startableFromLockScreen`

Konstruktory:

```java
WatchApplication(String name)
WatchApplication(String name, boolean canInstallApplication, boolean startableFromLockScreen)
```

Getter metody:

```java
getName()
canInstallApplication()
startableFromLockScreen()
```

`equals` a `hashCode` porovnavaju vsetky 3 atributy.

## Predinstalovane aplikacie

Kazde nove `SmartWatch` maju mat presne tieto aplikacie:

- `Store`, vie instalovat aplikacie, neda sa spustit zo zamknutej obrazovky
- `Timer`, nevie instalovat aplikacie, da sa spustit zo zamknutej obrazovky
- `Weather`, nevie instalovat aplikacie, neda sa spustit zo zamknutej obrazovky

## Spravanie stavov

### Off

Zaciatocny stav hodiniek.

- `sideButtonPressed()` -> stav `WaitingForPin`
- vsetko ostatne nic nerobi

### WaitingForPin

- `pinEntered("1111")` -> stav `Menu`
- zly pin -> ostava `WaitingForPin`
- `start(app)` spusti iba aplikaciu, ktora ma `startableFromLockScreen() == true`
- ked sa aplikacia spusti odtialto, stav bude `ActivityRunning`
- `backButtonPressed()`, `install(...)`, `uninstall(...)` nic nerobia

### Menu

- `start(app)` spusti aplikaciu, ak je nainstalovana
- po spusteni je stav `ActivityRunning`
- `uninstall(app)` odinstaluje aplikaciu, ak je nainstalovana
- `sideButtonPressed()` -> stav `Off`
- `backButtonPressed()`, `pinEntered(...)`, `install(...)` nic nerobia

### ActivityRunning

- `backButtonPressed()`:
  - ak bola aplikacia spustena z `WaitingForPin`, vrati sa do `Off`
  - ak bola aplikacia spustena z `Menu`, vrati sa do `Menu`
- `sideButtonPressed()` -> stav `Off`
- `install(app)` funguje iba vtedy, ak aktualne beziaca aplikacia vie instalovat aplikacie
- `start(...)`, `uninstall(...)`, `pinEntered(...)` nic nerobia

## Verejne metody SmartWatch

Do `SmartWatch` nepridavaj dalsie public metody. Test ocakava tieto:

```java
sideButtonPressed()
backButtonPressed()
pinEntered(String pin)
start(WatchApplication application)
install(WatchApplication application)
uninstall(WatchApplication application)
getStateName()
getRunningApplication()
getInstalledApplications()
```

## Presny postup riesenia

1. Najprv sprav `WatchApplication`.
   - nastav atributy v konstruktoroch
   - sprav gettery
   - sprav `equals` a `hashCode`

2. Potom sprav `SmartWatch`.
   - pridaj private atribut `State state`
   - pridaj zoznam aplikacii
   - pridaj `runningApplication`
   - v konstruktore nastav stav `Off`
   - v konstruktore pridaj 3 predinstalovane aplikacie

3. Public metody v `SmartWatch` len posuvaju pracu do stavu.
   - napriklad `sideButtonPressed()` zavola `state.sideButtonPressed()`
   - `getStateName()` vrati meno aktualneho stavu

4. Potom sprav triedu `State`.
   - bude abstraktna
   - bude mat protected `SmartWatch watch`
   - defaultne metody mozu nic nerobit
   - kazdy konkretny stav prepise iba to, co ma robit inak

5. Potom ries stavy v tomto poradi:
   - `Off`
   - `WaitingForPin`
   - `Menu`
   - `ActivityRunning`

6. Nakoniec ries instalovanie a odinstalovanie.
   - instalovat sa da iba ked bezi `Store`
   - odinstalovat sa da iba z `Menu`

## Rada

Nesnaz sa spravit vsetko naraz.
Rozbehni najprv prve testy: konstruktor, aplikacie, prechod `Off -> WaitingForPin -> Menu`.
Potom pridavaj dalsie spravanie.
