public class SimulationConfig {
    private int initialPlanesPerTeam = 5;
    private int targetPlanesPerTeam  = 5;
    private int windSpawnChance      = 50;
    private int windDuration         = 50;

    private int   startingAmmo = 10;
    private float baseSpeed    = 8.5f;  // Zmieniona wartość bazowa
    private int   startingHp   = 3;
    private float projectileSpeed = 16.0f; // Szybkość pocisków dopasowana do bazy 8.5f

    private int airportCapacity = 5;
    private int maintenanceTime = 40;

    private float speedVariance      = 2.0f;
    private float detectionRangeMin  = 80.0f;
    private float detectionRangeMax  = 160.0f;
    private float fightRange         = 60.0f;
    private int   shotCooldown       = 5;
    private int   evadeDuration      = 15;
    private int   fightDuration      = 30;
    private int   evadeHpThreshold   = 2;

    // --- GETTERY DLA BLUEPLANE, REDPLANE I AIRPORT ---

    public int getInitialPlanesPerTeam() {
        return initialPlanesPerTeam;
    }

    public int getTargetPlanesPerTeam() {
        return targetPlanesPerTeam;
    }

    public int getWindSpawnChance() {
        return windSpawnChance;
    }

    public int getWindDuration() {
        return windDuration;
    }

    public float getBaseSpeed() {
        return baseSpeed;
    }

    public float getSpeedVariance() {
        return speedVariance;
    }

    public float getDetectionRangeMin() {
        return detectionRangeMin;
    }

    public float getDetectionRangeMax() {
        return detectionRangeMax;
    }

    public int getStartingHp() {
        return startingHp;
    }

    public int getStartingAmmo() {
        return startingAmmo;
    }

    public float getFightRange() {
        return fightRange;
    }

    public int getShotCooldown() {
        return shotCooldown;
    }

    public int getEvadeDuration() {
        return evadeDuration;
    }

    public int getFightDuration() {
        return fightDuration;
    }

    public int getEvadeHpThreshold() {
        return evadeHpThreshold;
    }

    public float getProjectileSpeed() {
        return projectileSpeed;
    }

    public int getAirportCapacity() {
        return airportCapacity;
    }

    public int getMaintenanceTime() {
        return maintenanceTime;
    }

    // --- ALIAS COPATYBILNOŚCIOWY DLA STARYCH FUNKCJI ---
    public double getPlaneSpeed() { return baseSpeed; }
    public int getMaxAmmo() { return startingAmmo; }
    public int getMaxHp() { return startingHp; }


    // --- SETTERY DLA PANELU SETUP (SimulationApp) ---

    public void setInitialPlanesPerTeam(int val) {
        this.initialPlanesPerTeam = val;
        this.targetPlanesPerTeam = val;
    }

    public void setTargetPlanesPerTeam(int val) {
        this.targetPlanesPerTeam = val;
    }

    public void setPlaneSpeed(double planeSpeed) {
        this.baseSpeed = (float) planeSpeed;
    }

    public void setProjectileSpeed(double projectileSpeed) {
        this.projectileSpeed = (float) projectileSpeed;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.startingAmmo = maxAmmo;
    }

    public void setMaxHp(int maxHp) {
        this.startingHp = maxHp;
    }

    public void setDetectionRangeMin(float val) {
        this.detectionRangeMin = val;
    }

    public void setDetectionRangeMax(float val) {
        this.detectionRangeMax = val;
    }

    public void setFightRange(float val) {
        this.fightRange = val;
    }
}