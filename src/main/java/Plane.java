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
    protected float detectionRange;
    protected int evadeDuration;
    protected int fightDuration;
    protected int shotCooldown = 0;
    protected int cooldownDuration = 5;

    public Plane(int id, float x, float y, float baseX, float baseY, float detectionRange, int evadeDuration, int fightDuration) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.baseX = baseX;
        this.baseY = baseY;
        this.detectionRange = detectionRange;
        this.evadeDuration = evadeDuration;
        this.fightDuration = fightDuration;
        this.state = PlaneState.FLYING;
    }

    public void step(Board board) {
        if (this.state == PlaneState.DEAD) return;

        if (this.shotCooldown > 0) {
            this.shotCooldown--;
        }

        this.target = board.getClosestEnemy(this);

        if (this.state == PlaneState.EVADING) {
            this.fuel -= 1.5f;
            this.evadeTimer--;
            if (this.evadeTimer <= 0) {
                if (worthFlyingToBase()) {
                    this.state = PlaneState.RETURNING_TO_BASE;
                } else {
                    this.state = PlaneState.FLYING;
                }
            }
            return;
        }

        if (this.state == PlaneState.RETURNING_TO_BASE) {
            this.fuel -= 1.0f;
            return;
        }

        if (worthFlyingToBase()) {
            this.state = PlaneState.RETURNING_TO_BASE;
            this.fuel -= 1.0f;
            return;
        }


        if (this.state == PlaneState.FLYING) {
            this.fuel -= 1.0f;
            if (this.target != null && isNearEnemy(this.target)) {
                this.state = PlaneState.FIGHTING;
                this.fightTimer = this.fightDuration;
            }
        }

        else if (this.state == PlaneState.FIGHTING) {
            this.fuel -= 1.5f;
            this.fightTimer--;

            if (this.target != null && this.ammo > 0) {
                if (this.shotCooldown == 0) {
                    shoot(this.target.x, this.target.y, board);
                    this.ammo--;
                    this.shotCooldown = this.cooldownDuration;
                }
            }

            if (this.fightTimer <= 0) {
                if (worthFlyingToBase()) {
                    this.state = PlaneState.RETURNING_TO_BASE;
                } else {
                    this.state = PlaneState.FLYING;
                }
            }
        }
    }


    private boolean isNearEnemy(Plane enemy) {
        float dx = this.x - enemy.x;
        float dy = this.y - enemy.y;
        return Math.sqrt(dx * dx + dy * dy) < detectionRange;
    }
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
                this.shotCooldown = 0;
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

    public void shoot(float targetX, float targetY, Board board) {
        if (this.ammo > 0) {
            board.addProjectile(new Projectile(this.x, this.y, targetX, targetY, this));
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