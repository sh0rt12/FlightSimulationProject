public class SimulationConfig {

    // Samoloty
    private int   initialPlanesPerTeam;
    private int   targetPlanesPerTeam;
    private float baseSpeed;
    private float speedVariance;
    private int   startingHp;
    private int   startingAmmo;
    private float detectionRangeMin;
    private float detectionRangeMax;
    private int   shotCooldown;

    // Walka
    private float fightRange;         // odległość wejścia w FIGHTING
    private int   evadeDuration;      // ile kroków trwa manewr EVADING
    private int   fightDuration;      // ile kroków trwa FIGHTING zanim samolot oceni sytuację
    private int   evadeHpThreshold;   // przy ilu HP samolot zaczyna unikać po trafieniu

    // Lotnisko
    private int maintenanceTime;
    private int airportCapacity;

    // Wiatr
    private int windSpawnChance;
    private int windDuration;

    public SimulationConfig() {
        this.initialPlanesPerTeam = 5;
        this.targetPlanesPerTeam  = 5;
        this.baseSpeed            = 8.5f;
        this.speedVariance        = 2.0f;
        this.startingHp           = 3;
        this.startingAmmo         = 10;
        this.detectionRangeMin    = 80.0f;
        this.detectionRangeMax    = 160.0f;
        this.shotCooldown         = 5;
        this.fightRange           = 60.0f;
        this.evadeDuration        = 15;
        this.fightDuration        = 30;
        this.evadeHpThreshold     = 2;
        this.maintenanceTime      = 40;
        this.airportCapacity      = 5;
        this.windSpawnChance      = 50;
        this.windDuration         = 50;
    }

    // Gettery
    public int   getInitialPlanesPerTeam()  { return initialPlanesPerTeam; }
    public int   getTargetPlanesPerTeam()   { return targetPlanesPerTeam; }
    public float getBaseSpeed()             { return baseSpeed; }
    public float getSpeedVariance()         { return speedVariance; }
    public int   getStartingHp()            { return startingHp; }
    public int   getStartingAmmo()          { return startingAmmo; }
    public float getDetectionRangeMin()     { return detectionRangeMin; }
    public float getDetectionRangeMax()     { return detectionRangeMax; }
    public int   getShotCooldown()          { return shotCooldown; }
    public float getFightRange()            { return fightRange; }
    public int   getEvadeDuration()         { return evadeDuration; }
    public int   getFightDuration()         { return fightDuration; }
    public int   getEvadeHpThreshold()      { return evadeHpThreshold; }
    public int   getMaintenanceTime()       { return maintenanceTime; }
    public int   getAirportCapacity()       { return airportCapacity; }
    public int   getWindSpawnChance()       { return windSpawnChance; }
    public int   getWindDuration()          { return windDuration; }

    // Settery - DO UI
    public SimulationConfig setInitialPlanesPerTeam(int v)  { this.initialPlanesPerTeam = v; return this; }
    public SimulationConfig setTargetPlanesPerTeam(int v)   { this.targetPlanesPerTeam  = v; return this; }
    public SimulationConfig setBaseSpeed(float v)           { this.baseSpeed            = v; return this; }
    public SimulationConfig setSpeedVariance(float v)       { this.speedVariance        = v; return this; }
    public SimulationConfig setStartingHp(int v)            { this.startingHp           = v; return this; }
    public SimulationConfig setStartingAmmo(int v)          { this.startingAmmo         = v; return this; }
    public SimulationConfig setDetectionRangeMin(float v)   { this.detectionRangeMin    = v; return this; }
    public SimulationConfig setDetectionRangeMax(float v)   { this.detectionRangeMax    = v; return this; }
    public SimulationConfig setShotCooldown(int v)          { this.shotCooldown         = v; return this; }
    public SimulationConfig setFightRange(float v)          { this.fightRange           = v; return this; }
    public SimulationConfig setEvadeDuration(int v)         { this.evadeDuration        = v; return this; }
    public SimulationConfig setFightDuration(int v)         { this.fightDuration        = v; return this; }
    public SimulationConfig setEvadeHpThreshold(int v)      { this.evadeHpThreshold     = v; return this; }
    public SimulationConfig setMaintenanceTime(int v)       { this.maintenanceTime      = v; return this; }
    public SimulationConfig setAirportCapacity(int v)       { this.airportCapacity      = v; return this; }
    public SimulationConfig setWindSpawnChance(int v)       { this.windSpawnChance      = v; return this; }
    public SimulationConfig setWindDuration(int v)          { this.windDuration         = v; return this; }
}
