//NIEZMIENNE PARAMETRY SAMOLOTU PODCZAS SYMULACJI

public class PlaneStats {
    public final float baseSpeed;
    public final int   maxHp;
    public final int   maxAmmo;
    public final float maxFuel;
    public final float detectionRange;
    public final int   shotCooldown;
    public final int   evadeDuration;
    public final int   fightDuration;

    public PlaneStats(float baseSpeed, int maxHp, int maxAmmo, float maxFuel,
                      float detectionRange, int shotCooldown,
                      int evadeDuration, int fightDuration) {
        this.baseSpeed      = baseSpeed;
        this.maxHp          = maxHp;
        this.maxAmmo        = maxAmmo;
        this.maxFuel        = maxFuel;
        this.detectionRange = detectionRange;
        this.shotCooldown   = shotCooldown;
        this.evadeDuration  = evadeDuration;
        this.fightDuration  = fightDuration;
    }
}
