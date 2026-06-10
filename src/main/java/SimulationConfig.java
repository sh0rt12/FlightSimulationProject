public class SimulationConfig {
    private int   initialPlanesPerTeam = 7;
    private int   targetPlanesPerTeam  = 7;
    private int   windSpawnChance      = 50;
    private int   windDuration         = 50;
    private int   startingAmmo         = 15;
    private float baseSpeed            = 8.5f;
    private int   startingHp           = 3;
    private float projectileSpeed      = 18.0f;
    private int   airportCapacity      = 5;
    private int   maintenanceTime      = 40;
    private float speedVariance        = 2.0f;
    private float detectionRangeMin    = 100.0f;
    private float detectionRangeMax    = 180.0f;
    private float fightRange           = 38.0f;
    private int   shotCooldown         = 5;
    private int   evadeDuration        = 25;
    private int   fightDuration        = 30;
    private int   evadeHpThreshold     = 2;

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
}