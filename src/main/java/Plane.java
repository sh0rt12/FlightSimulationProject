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
    protected float baseX;
    protected float baseY;

    public Plane(int id, float x, float y, float baseX, float baseY) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.baseX = baseX;
        this.baseY = baseY;
        this.state = PlaneState.FLYING;
    }

    public abstract void step(Board board);

    public void setState(PlaneState state) {
        this.state = state;
    }

    public float distanceToBase() {
        float dx = this.baseX - this.x;
        float dy = this.baseY - this.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public boolean worthFlyingToBase() {
        float turnsToBase = distanceToBase() / this.currentSpeed;
        float fuelNeeded = turnsToBase * 1.0f;
        return this.fuel <= fuelNeeded + 10;
    }

    public void move() {
        updateTimers();

        if (this.hp <= 0 || this.fuel <= 0) {
            this.currentSpeed = 0;
            this.state = PlaneState.DEAD;
            return;
        }

        if (this.fuel <= 10) {
            if (tryEscapeOffMap()) return;
        }

        if (this.target != null && (this.state == PlaneState.FIGHTING || this.state == PlaneState.FLYING)) {
            float dx = this.target.x - this.x;
            float dy = this.target.y - this.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance > 0) {
                this.x += (dx / distance) * this.currentSpeed;
                this.y += (dy / distance) * this.currentSpeed;
            }
        } else if (this.state == PlaneState.RETURNING_TO_BASE) {
            float dx = this.baseX - this.x;
            float dy = this.baseY - this.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < 20.0) {
                this.fuel = this.maxFuel;
                this.ammo = this.maxAmmo;
                this.hp = 3;
                this.state = PlaneState.FLYING;
                return;
            }
            if (distance > 0) {
                this.x += (dx / distance) * this.currentSpeed;
                this.y += (dy / distance) * this.currentSpeed;
            }
        } else if (this.state == PlaneState.EVADING) {
            float dx = this.target != null ? this.target.x - this.x : 0;
            float dy = this.target != null ? this.target.y - this.y : 0;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance > 0) {
                float perpX = (float) (-dy / distance);
                float perpY = (float) (dx / distance);
                this.x += perpX * this.currentSpeed;
                this.y += perpY * this.currentSpeed;
            }
        }
    }

    private boolean tryEscapeOffMap() {
        float dx = this.baseX - this.x;
        float dy = this.baseY - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance > 0) {
            float escapeX = (float) (-dx / distance);
            float escapeY = (float) (-dy / distance);
            this.x += escapeX * this.currentSpeed;
            this.y += escapeY * this.currentSpeed;
        }
        if (this.x < 0 || this.x > 1000 || this.y < 0 || this.y > 1000) {
            this.state = PlaneState.DEAD;
            return true;
        }
        return false;
    }

    public void shoot(float targetX, float targetY) {
        if (this.target != null && this.ammo > 0) {
            this.target.takeDamage(1);
        }
    }

    public void takeDamage(int d) {
        this.hp -= d;
        if (this.hp > 0) {
            this.state = PlaneState.EVADING;
            this.evadeTimer = 3;
        }
    }

    public boolean isLowOnFuel() {
        return this.fuel < (this.maxFuel * 0.15f);
    }

    private void updateTimers() {
        if (this.fightTimer > 0) this.fightTimer--;
        if (this.evadeTimer > 0) this.evadeTimer--;
    }
}