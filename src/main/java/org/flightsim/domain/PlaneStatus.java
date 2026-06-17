package org.flightsim.domain;

// Stan samolotu który zmienia się w trakcie symulacji — HP, paliwo, amunicja i timery.
// Celowo oddzielone od PlaneStats żeby było widać co jest zmienne a co stałe.
public class PlaneStatus {
    public int   hp;
    public int   ammo;
    public float fuel;
    public float currentSpeed;
    public int   shotCooldown; // ile kroków zostało do następnego strzału
    public int   fightTimer;   // jak długo samolot już walczy (po fightDuration odrywa się)
    public int   evadeTimer;   // krok w animacji manewru unikowego

    public double orbitAngle;  // kąt orbity wokół celu w trybie FIGHTING

    // +1 lub -1 — losowany przy wejściu w EVADE, żeby połowa leciała w lewo a połowa w prawo
    public float evadeDirection;

    public boolean hitThisStep; // flaga do decyzji o wejściu w EVADE po trafieniu

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
