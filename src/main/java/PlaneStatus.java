//ZMIENNE PARAMETRY SAMOLOTU PODCZAS SYMULACJI

public class PlaneStatus {
    public int   hp;
    public int   ammo;
    public float fuel;
    public float currentSpeed;
    public int   shotCooldown;
    public int   fightTimer;
    public int   evadeTimer;

    public PlaneStatus(int hp, int ammo, float fuel, float currentSpeed, int shotCooldown) {
        this.hp           = hp;
        this.ammo         = ammo;
        this.fuel         = fuel;
        this.currentSpeed = currentSpeed;
        this.shotCooldown = shotCooldown;
        this.fightTimer   = 0;
        this.evadeTimer   = 0;
    }
}
