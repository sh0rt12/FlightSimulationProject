public abstract class Plane {
    protected int id;
    protected float x;
    protected float y;
    protected float baseSpeed;
    protected float currentSpeed;
    protected int ammo;
    protected int maxAmmo;
    protected float fuel;
    protected float maxFuel;
    protected int hp;
    protected Plane target;
    protected PlaneState state;
    protected int fightTimer;
    protected int evadeTimer;


    public Plane(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.state = PlaneState.FLYING;
    }

    public abstract void step(Board board);

    public void move() {
        // Jeśli samolot stracił całe HP lub paliwo, to spada (nie rusza się)
        if (this.hp <= 0 || this.fuel <= 0) {
            this.currentSpeed = 0;
            return;
        }

        // Prosta, testowa fizyka wektorowa
        if (this.state == PlaneState.FIGHTING && this.target != null) {
            // Ruch w stronę celu (wroga)
            float dx = this.target.x - this.x;
            float dy = this.target.y - this.y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                this.x += (dx / distance) * this.currentSpeed;
                this.y += (dy / distance) * this.currentSpeed;
            }
        } else if (this.state == PlaneState.RETURNING_TO_BASE) {
            // Ruch w stronę bazy (na razie upraszczamy: baza jest w punkcie 0,0)
            float dx = 0 - this.x;
            float dy = 0 - this.y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                this.x += (dx / distance) * this.currentSpeed;
                this.y += (dy / distance) * this.currentSpeed;
            }
        } else {
            // Zwykły lot patrolowy (FLYING / EVADING) - niech lecą przed siebie w zależności od ID
            // Czerwone (nieparzyste) lecą w prawo, Niebieskie (parzyste) w lewo
            if (this.id % 2 != 0) {
                this.x += this.currentSpeed;
            } else {
                this.x -= this.currentSpeed;
            }
        }
    }

    public void shoot(float targetX, float targetY) {

    }

    public void takeDamage(int d) {

    }

    public boolean isLowOnFuel() { return false; }

    public boolean worthFlyingToBase() {return true; }

    private void updateTimers() {

    }
}