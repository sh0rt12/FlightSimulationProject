package org.flightsim.domain;

// Konfiguracja symulacji — wartości domyślne są zbalansowane pod 7v7.
// Wszystkie można zmienić z menu przed startem.
public class SimulationConfig {
    private int   initialPlanesPerTeam = 7;
    private int   targetPlanesPerTeam  = 7;  // docelowa liczba po respawnach
    private int   windSpawnChance      = 50;  // 1/50 szans na wiatr co krok
    private int   windDuration         = 50;  // kroków zanim wiatr ucichnie
    private int   startingAmmo         = 15;
    private float baseSpeed            = 8.5f;
    private int   startingHp           = 3;
    private float projectileSpeed      = 18.0f;
    private float fuelCapacity         = 100.0f;
    private int   airportCapacity      = 5;   // ile samolotów jednocześnie w hangarze
    private int   maintenanceTime      = 40;  // kroków w hangarze do pełnego serwisu
    private float speedVariance        = 2.0f; // losowy bonus do prędkości
    private float detectionRangeMin    = 100.0f;
    private float detectionRangeMax    = 180.0f;
    private float fightRange           = 38.0f;  // odległość przy której zaczyna orbita
    private int   shotCooldown         = 5;
    private int   evadeDuration        = 25;
    private int   fightDuration        = 30;     // po ilu krokach samolot odrywa się od walki
    private int   evadeHpThreshold     = 2;      // HP przy którym wchodzi EVADE po trafieniu

    public int   getInitialPlanesPerTeam() { return initialPlanesPerTeam; }
    public int   getTargetPlanesPerTeam()  { return targetPlanesPerTeam; }
    public int   getWindSpawnChance()      { return windSpawnChance; }
    public int   getWindDuration()         { return windDuration; }
    public float getBaseSpeed()            { return baseSpeed; }
    public float getSpeedVariance()        { return speedVariance; }
    public float getDetectionRangeMin()    { return detectionRangeMin; }
    public float getDetectionRangeMax()    { return detectionRangeMax; }
    public int   getStartingHp()           { return startingHp; }
    public int   getStartingAmmo()         { return startingAmmo; }
    public float getFightRange()           { return fightRange; }
    public int   getShotCooldown()         { return shotCooldown; }
    public int   getEvadeDuration()        { return evadeDuration; }
    public int   getFightDuration()        { return fightDuration; }
    public int   getEvadeHpThreshold()     { return evadeHpThreshold; }
    public float getProjectileSpeed()      { return projectileSpeed; }
    public float getFuelCapacity()         { return fuelCapacity; }
    public int   getAirportCapacity()      { return airportCapacity; }
    public int   getMaintenanceTime()      { return maintenanceTime; }

    public void setInitialPlanesPerTeam(int v)  { this.initialPlanesPerTeam = v; this.targetPlanesPerTeam = v; }
    public void setBaseSpeed(float v)           { this.baseSpeed            = v; }
    public void setStartingHp(int v)            { this.startingHp           = v; }
    public void setStartingAmmo(int v)          { this.startingAmmo         = v; }
    public void setDetectionRangeMin(float v)   { this.detectionRangeMin    = v; }
    public void setDetectionRangeMax(float v)   { this.detectionRangeMax    = v; }
    public void setFightRange(float v)          { this.fightRange           = v; }
    public void setProjectileSpeed(float v)     { this.projectileSpeed      = v; }
    public void setFuelCapacity(float v)        { this.fuelCapacity         = v; }
}
