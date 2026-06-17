package org.flightsim.domain;

import java.util.ArrayList;
import java.util.Random;

// Główny silnik — orkiestruje jeden krok symulacji.
// Pogoda i statystyki są w osobnych klasach żeby tu nie było bałaganu.
public class Simulation {
    private final Board            board;
    private final SimulationConfig config;
    private final Random           random = new Random();

    private final WeatherSystem weather;
    private final BattleStats   stats = new BattleStats();

    private int stepCount;

    // Łączna liczba stworzonych samolotów (wliczając respawny) — do statystyk
    private int totalRedPlanes  = 0;
    private int totalBluePlanes = 0;

    public Simulation(SimulationConfig config) {
        this.config  = config;
        this.weather = new WeatherSystem(config);
        this.board   = new Board();
        this.board.setSimulation(this);
        // lotniska na stałych pozycjach po lewej i prawej stronie
        this.board.getAirports().add(new Airport(50.0f,  500.0f, config));
        this.board.getAirports().add(new Airport(950.0f, 500.0f, config));
        this.stepCount = 0;
        initializePlanes();
    }

    private void initializePlanes() {
        for (int i = 0; i < config.getInitialPlanesPerTeam(); i++) {
            totalRedPlanes++;
            float y = 50.0f + random.nextFloat() * 900.0f;
            RedPlane red = new RedPlane(totalRedPlanes * 2 - 1, 100.0f, y, config);
            red.setState(PlaneState.FLYING);
            board.addPlane(red);
        }
        for (int i = 0; i < config.getInitialPlanesPerTeam(); i++) {
            totalBluePlanes++;
            float y = 50.0f + random.nextFloat() * 900.0f;
            BluePlane blue = new BluePlane(totalBluePlanes * 2, 900.0f, y, config);
            blue.setState(PlaneState.FLYING);
            board.addPlane(blue);
        }
    }

    public void step() {
        this.stepCount++;

        weather.update(stepCount);

        // iterujemy po kopii listy bo step() może modyfikować stan samolotów
        for (Plane plane : new ArrayList<>(board.getPlanes())) {
            if (plane.state != PlaneState.DEAD) {
                plane.step(board);
                plane.move(board);
            }
        }

        for (Projectile projectile : new ArrayList<>(board.getProjectiles())) {
            projectile.move();
        }

        for (Airport airport : board.getAirports()) {
            airport.processTurn();
        }

        board.checkCollisions();
        board.getPlanes().removeIf(p -> p.state == PlaneState.DEAD);
        board.getProjectiles().removeIf(p -> p.isOutOfBoard(1000, 1000));

        spawnPlanes(); // uzupełnia do docelowej liczby jeśli ktoś zginął
    }

    // Respawn — jedna maszyna na krok żeby nie było nagłego "teleportu" całej floty
    private void spawnPlanes() {
        int currentRed  = 0;
        int currentBlue = 0;

        for (Plane p : board.getPlanes()) {
            if (p.isRedTeam()) currentRed++;
            else               currentBlue++;
        }

        int target = config.getTargetPlanesPerTeam();

        if (currentRed < target) {
            totalRedPlanes++;
            float y = 50.0f + random.nextFloat() * 900.0f;
            RedPlane newRed = new RedPlane(totalRedPlanes * 2 - 1, 100.0f, y, config);
            newRed.setState(PlaneState.FLYING);
            board.addPlane(newRed);
        }

        if (currentBlue < target) {
            totalBluePlanes++;
            float y = 50.0f + random.nextFloat() * 900.0f;
            BluePlane newBlue = new BluePlane(totalBluePlanes * 2, 900.0f, y, config);
            newBlue.setState(PlaneState.FLYING);
            board.addPlane(newBlue);
        }
    }

    // Delegacje do WeatherSystem
    public double  getWindMultiplier(Plane p) { return weather.getWindMultiplier(p); }
    public boolean isWeatherActive()          { return weather.isActive(); }
    public int     getCurrentWindType()       { return weather.getWindType(); }
    public int     getWindDirection()         { return weather.getWindDirection(); }

    // Delegacje do BattleStats
    public void   incrementRedShots()  { stats.incrementRedShots(); }
    public void   incrementRedHits()   { stats.incrementRedHits(); }
    public void   incrementBlueShots() { stats.incrementBlueShots(); }
    public void   incrementBlueHits()  { stats.incrementBlueHits(); }
    public double getRedAccuracy()     { return stats.getRedAccuracy(); }
    public double getBlueAccuracy()    { return stats.getBlueAccuracy(); }

    public Board            getBoard()           { return board; }
    public SimulationConfig getConfig()          { return config; }
    public int              getStepCount()       { return stepCount; }
    public int              getTotalRedPlanes()  { return totalRedPlanes; }
    public int              getTotalBluePlanes() { return totalBluePlanes; }
}
