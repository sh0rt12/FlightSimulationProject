public abstract class Plane {
    protected int id;
    public float x;
    public float y;
    protected float baseSpeed;
    protected float currentSpeed;
    protected int ammo;
    protected int maxAmmo;
    protected float fuel;
    protected float maxFuel;
    protected int hp;
    protected Plane target;
    protected PlaneState state;
    protected int fightTimer; // tego uzyjemy jak bedziemy robic bardziej zaawansowany system uników
    protected int evadeTimer; // tego tez
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
        if (this.state == PlaneState.DEAD || this.state == PlaneState.PARKED) return;

        if (this.shotCooldown > 0) {
            this.shotCooldown--;
        }

        this.target = board.getClosestEnemy(this);

        if (this.hp <= 1 || this.ammo <= 0) {
            this.state = PlaneState.RETURNING_TO_BASE;
        } else if (this.state == PlaneState.RETURNING_TO_BASE && this.hp == 3 && this.ammo > 0) {
            this.state = PlaneState.FLYING;
        }

        if (this.state == PlaneState.FLYING && this.target != null && this.target.state != PlaneState.DEAD) {
            float dx = this.target.x - this.x;
            float dy = this.target.y - this.y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < this.detectionRange && this.shotCooldown == 0) {
                shoot(this.target.x, this.target.y, board);
                this.shotCooldown = this.cooldownDuration;
                this.ammo--;
            }
        }
    }

    public void move(Board board) {
        if (this.state == PlaneState.DEAD || this.state == PlaneState.PARKED) return;

        if (this.state == PlaneState.RETURNING_TO_BASE) {
            float dx = this.baseX - this.x;
            float dy = this.baseY - this.y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < 15.0) {
                Airport airport = board.getAirportFor(this);
                if (airport.canDock()) {
                    airport.dockPlane(this);
                    return;
                } else {
                    tryEscapeOffMap();
                    return;
                }
            }

            if (distance > 0) {
                this.x += (float) ((dx / distance) * this.currentSpeed);
                this.y += (float) ((dy / distance) * this.currentSpeed);
            }
        } else {
            if (this.target != null && this.target.state != PlaneState.DEAD) {
                float dx = this.target.x - this.x;
                float dy = this.target.y - this.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance > 0) {
                    this.x += (float) ((dx / distance) * this.currentSpeed);
                    this.y += (float) ((dy / distance) * this.currentSpeed);
                }
            } else {
                boolean isRed = (this instanceof RedPlane);
                this.x += isRed ? this.currentSpeed : -this.currentSpeed;
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
            float dx = targetX - this.x;
            float dy = targetY - this.y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            float spawnX = this.x;
            float spawnY = this.y;
            if (dist > 0) {
                spawnX += (float) ((dx / dist) * 16.0);
                spawnY += (float) ((dy / dist) * 16.0);
            }

            board.addProjectile(new Projectile(spawnX, spawnY, targetX, targetY, this));
        }
    }

    public void takeDamage(int d) {
        this.hp -= d;
        if (this.hp <= 0) {
            this.state = PlaneState.DEAD;
        }
    }

    public void setState(PlaneState state) {
        this.state = state;
    }
}