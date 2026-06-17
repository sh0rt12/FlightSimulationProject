package org.flightsim.domain;

// Dane statyczne samolotu — nadawane przy tworzeniu i nigdy nie zmieniane.
// Dzięki temu nie trzeba pilnować żeby coś przypadkowo nie nadpisało baseSpeed w trakcie walki.
public class PlaneStats {
    public final float baseSpeed;
    public final int   maxHp;
    public final int   maxAmmo;
    public final float maxFuel;
    public final float detectionRange;
    public final float fightRange;
    public final int   shotCooldown;
    public final int   evadeDuration;
    public final int   fightDuration;
    public final int   evadeHpThreshold; // poniżej tego HP samolot próbuje uciec

    public PlaneStats(float baseSpeed, int maxHp, int maxAmmo, float maxFuel,
                      float detectionRange, float fightRange, int shotCooldown,
                      int evadeDuration, int fightDuration, int evadeHpThreshold) {
        this.baseSpeed        = baseSpeed;
        this.maxHp            = maxHp;
        this.maxAmmo          = maxAmmo;
        this.maxFuel          = maxFuel;
        this.detectionRange   = detectionRange;
        this.fightRange       = fightRange;
        this.shotCooldown     = shotCooldown;
        this.evadeDuration    = evadeDuration;
        this.fightDuration    = fightDuration;
        this.evadeHpThreshold = evadeHpThreshold;
    }
}
