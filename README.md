# Flight Simulation

Symulacja powietrzna 2D napisana w Javie z JavaFX. Dwie drużyny samolotów (czerwoni i niebiescy) walczą ze sobą — każdy samolot ma własne AI — każdy samolot ma własne AI z maszyną stanów, zarządza paliwem i amunicją, ląduje na lotnisku po serwis i wraca do walki.

Projekt: Bartosz Niski i Krzysztof Przybysz

---

## Wymagania

- Java 17+
- Maven 3.8+ **lub** Gradle 8+

JavaFX jest pobierany automatycznie przez Maven/Gradle z Maven Central — nie trzeba nic instalować ręcznie.

---

## Quick Start

### Maven

```bash
# sklonuj repozytorium
git clone https://github.com/sh0rt12/FlightSimulationProject.git
cd flight-simulation

# uruchom bezpośrednio
mvn javafx:run

# lub zbuduj fat JAR i uruchom
mvn package
java -jar target/flight-simulation-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Gradle

```bash
git clone https://github.com/sh0rt12/FlightSimulationProject.git
cd flight-simulation

# uruchom bezpośrednio
gradle run

# lub zbuduj JAR
gradle jar
```

---

## Struktura projektu

```
flight-simulation/
├── pom.xml                          # konfiguracja Maven
├── build.gradle                     # konfiguracja Gradle
├── settings.gradle
└── src/main/java/org/flightsim/
    ├── domain/                      # logika symulacji (bez UI)
    │   ├── Simulation.java          # główny silnik, zarządza pętlą/krokami symulacji lub koordynuje kroki symulacji
    │   ├── Board.java               # plansza: samoloty, pociski, lotniska
    │   ├── Plane.java               # klasa bazowa samolotu
    │   ├── RedPlane.java            # drużyna czerwonych
    │   ├── BluePlane.java           # drużyna niebieskich
    │   ├── PlaneAI.java             # maszyna stanów AI
    │   ├── PlaneMovement.java       # wektory ruchu, separacja, wiatr
    │   ├── PlaneStats.java          # stałe parametry (prędkość, HP, zasięg)
    │   ├── PlaneStatus.java         # zmienne parametry (paliwo, amunicja, timery)
    │   ├── PlaneState.java          # enum stanów (FLYING, FIGHTING, EVADING, ...)
    │   ├── Projectile.java          # pocisk
    │   ├── Airport.java             # lotnisko z serwisem
    │   ├── WeatherSystem.java       # losowy wiatr wpływający na prędkość
    │   ├── BattleStats.java         # liczniki strzałów i trafień
    │   └── SimulationConfig.java    # konfiguracja z domyślnymi wartościami
    └── ui/                          # warstwa graficzna (JavaFX)
        ├── SimulationApp.java       # punkt wejścia, menu konfiguracji
        ├── SimulationPanel.java     # canvas + pętla animacji
        ├── StatsPanel.java          # panel statystyk po prawej
        ├── PlaneRenderer.java       # rysowanie samolotu z obrotem i płomieniem
        ├── GameAssets.java          # ładowanie grafik PNG/GIF
        └── Explosion.java           # 8-klatkowa animacja wybuchu
```

---

## Sample Run

Po uruchomieniu pojawia się menu konfiguracji:

```
Liczba samolotów na drużynę: 7
Ilość amunicji:              15
Szybkość samolotów:          8.5
Punkty zdrowia (HP):         3
Szybkość pocisków:           18.0
Pojemność paliwa:            100.0
Zasięg wzroku (min):         100.0
Zasięg wzroku (max):         180.0
Zasięg walki:                38.0
```

Po kliknięciu **URUCHOM** otwiera się okno symulacji 1320×1000:

- lewa strona — **lotnisko czerwonych** (x≈50)
- prawa strona — **lotnisko niebieskich** (x≈950)
- samoloty startują, szukają wrogów, wchodzą w orbity bojowe, strzelają
- zestrzelony samolot wybucha i respawnuje po chwili przy swoim lotnisku
- gdy samolotowi skończy się paliwo lub amunicja, wraca do bazy na serwis (~40 kroków)
- co jakiś czas pojawia się wiatr (widoczny jako nakładka GIF) spowalniający lub przyspieszający samoloty

Panel po prawej pokazuje na żywo:
- numer kroku symulacji
- aktualną pogodę (kierunek i siłę wiatru)
- liczbę aktywnych samolotów i łączną liczbę stworzonych dla każdej drużyny
- celność strzałów (%)

---

## Parametry konfiguracyjne

| Parametr | Opis | Domyślnie |
|---|---|---|
| Liczba samolotów | Liczba maszyn w każdej drużynie | 7 |
| Amunicja | Strzały na pełnym załadunku | 15 |
| Prędkość | Bazowa prędkość px/krok | 8.5 |
| HP | Punkty życia | 3 |
| Prędkość pocisków | px/krok | 18.0 |
| Paliwo | Pojemność baku | 100.0 |
| Zasięg wzroku | Min–max zasięg wykrywania wroga | 100–180 |
| Zasięg walki | Odległość wejścia w tryb orbity | 38.0 |

---

## Grafiki (opcjonalne)

Symulacja działa bez żadnych plików graficznych — zamiast nich rysowane są kolorowe kształty zastępcze. Jeśli chcesz dodać własne grafiki, umieść je w `src/main/resources/`:

```
RED AIRPORT.png
BLUE AIRPORT.png
FIGHTER JET RED FLYING.png
FIGHTER JET RED PARKED.png
FIGHTER JET BLUE FLYING.png
FIGHTER JET BLUE PARKED.png
WIND 5%.gif
WIND 10%.gif
WIND 15%.gif
```

---

## Licencja

MIT
