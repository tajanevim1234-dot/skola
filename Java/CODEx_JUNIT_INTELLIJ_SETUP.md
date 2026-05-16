# JUnit setup pre tieto OOP projekty

Pouzivatel chce JUnit testy spustat rucne v IntelliJ cez zelene sipky pri testoch
alebo cez IntelliJ run configuration. Nechce pomocne command skripty typu
`run-tests.ps1`, `run-tests.bat`, ani vlastny `SimpleJUnitRunner`.

Preferovany setup ako v `OT_2025/oop_skuska_opravna`:

- `src` je production source folder
- `test` je test source folder
- modul `.iml` obsahuje:
  - `src` ako `isTestSource="false"`
  - `test` ako `isTestSource="true"`
  - kniznicu `junit.jupiter`
- `.idea/libraries/junit_jupiter.xml` obsahuje JUnit Jupiter 5.9.3 z Maven repository
- volitelne pridat `.idea/runConfigurations/<TestClass>.xml` pre hlavnu testovaciu triedu
- volitelne pridat `pom.xml`, aby IntelliJ vedel nacitat JUnit dependency cez Maven

Pri podobnej poziadavke nerobit manualny test runner. Najprv skontrolovat a opravit:

1. `.iml`
2. `.idea/libraries/junit_jupiter.xml`
3. `.idea/runConfigurations/*.xml`
4. `pom.xml`
5. ci je test trieda v `test/...` a ma `org.junit.jupiter.api.Test`

Priklad pre `oop2024_skuska_opravna`:

- test trieda: `sk.stuba.fei.uim.oop.ExamTest`
- modul: `oop2024_skuska_opravna`
- package: `sk.stuba.fei.uim.oop`
