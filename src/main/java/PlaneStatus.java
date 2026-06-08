//ZMIENNE ELEMENTY SAMOLOTU W TRAKCIE SYMULACJI

public class PlaneStatus {
    public int   hp;
    public int   ammo;
    public float fuel;
    public float currentSpeed;
    public int   shotCooldown;
    public int   fightTimer;
    public int   evadeTimer;


    public double orbitAngle;

    // Kierunek manewru ucieczki (prostopadle do lotu): +1 lub -1
    public float evadeDirection;

    public boolean hitThisStep;

    public PlaneStatus(int hp, int ammo, float fuel, float currentSpeed, int shotCooldown) {
        this.hp             = hp;
        this.ammo           = ammo;
        this.fuel           = fuel;
        this.currentSpeed   = currentSpeed;
        this.shotCooldown   = shotCooldown;
        this.fightTimer     = 0;
        this.evadeTimer     = 0;
        this.orbitAngle     = 0.0;
        this.evadeDirection = 1.0f;
        this.hitThisStep    = false;
    }
}
