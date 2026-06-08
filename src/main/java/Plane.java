public abstract class Plane {
    protected final int id;
    public float x;
    public float y;
    public final float baseX;
    public final float baseY;

    protected final PlaneStats stats;
    protected final PlaneStatus status;

    protected Plane target;
    protected PlaneState state;

    protected float lastVx = 0;
    protected float lastVy = 0;

    public Plane(int id, float x, float y, float baseX, float baseY, PlaneStats stats, PlaneStatus status) {
        this.id     = id;
        this.x      = x;
        this.y      = y;
        this.baseX  = baseX;
        this.baseY  = baseY;
        this.stats  = stats;
        this.status = status;
        this.state  = PlaneState.FLYING;
    }

    // gettery do Airport.launchPlane()
    public float getBaseSpeed() { return stats.baseSpeed; }
    public int   getMaxAmmo()   { return stats.maxAmmo; }
    public float getMaxFuel()   { return stats.maxFuel; }
    public int   getMaxHp()     { return stats.maxHp; }

    public void setCurrentSpeed(float v) { status.currentSpeed = v; }

    public float getVx() { return this.lastVx; }
    public float getVy() { return this.lastVy; }

    public void step(Board board) {
        if (this.state == PlaneState.DEAD || this.state == PlaneState.PARKED) return;

        if (this.status.shotCooldown > 0) {
            this.status.shotCooldown--;
        }

        this.target = board.getClosestEnemy(this);

        if (this.status.hp <= 1 || this.status.ammo <= 0) {
            this.state = PlaneState.RETURNING_TO_BASE;
        } else if (this.state == PlaneState.RETURNING_TO_BASE
                && this.status.hp == this.stats.maxHp
                && this.status.ammo > 0) {
            this.state = PlaneState.FLYING;
        }

        if (this.state == PlaneState.FLYING
                && this.target != null
                && this.target.state != PlaneState.DEAD) {

            float dx   = this.target.x - this.x;
            float dy   = this.target.y - this.y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < this.stats.detectionRange && this.status.shotCooldown == 0) {
                shoot(this.target.x, this.target.y, board);
                this.status.shotCooldown = this.stats.shotCooldown;
                this.status.ammo--;
            }
        }
    }

    public void move(Board board) {
        if (this.state == PlaneState.DEAD || this.state == PlaneState.PARKED) return;

        float moveX = 0;
        float moveY = 0;

        if (this.state == PlaneState.RETURNING_TO_BASE) {
            float dx       = this.baseX - this.x;
            float dy       = this.baseY - this.y;
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
                moveX = (float) (dx / distance);
                moveY = (float) (dy / distance);
            }
        } else {
            if (this.target != null && this.target.state != PlaneState.DEAD) {
                float dx       = this.target.x - this.x;
                float dy       = this.target.y - this.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance > 0) {
                    moveX = (float) (dx / distance);
                    moveY = (float) (dy / distance);
                }
            } else {
                moveX = isRedTeam() ? 1.0f : -1.0f;
                moveY = 0.0f;
            }
        }

        this.lastVx = moveX;
        this.lastVy = moveY;

        double windMod = (board.getSimulation() != null)
                ? board.getSimulation().getWindMultiplier(this)
                : 1.0;

        this.x += (float) (moveX * this.status.currentSpeed * windMod);
        this.y += (float) (moveY * this.status.currentSpeed * windMod);
    }

    private void tryEscapeOffMap() {
        float dx       = this.baseX - this.x;
        float dy       = this.baseY - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            float escapeX = (float) (-dx / distance);
            float escapeY = (float) (-dy / distance);
            this.lastVx = escapeX;
            this.lastVy = escapeY;
            this.x += escapeX * this.status.currentSpeed;
            this.y += escapeY * this.status.currentSpeed;
        }

        if (this.x < 0 || this.x > 1000 || this.y < 0 || this.y > 1000) {
            this.state = PlaneState.DEAD;
        }
    }

    public void shoot(float targetX, float targetY, Board board) {
        if (this.status.ammo <= 0) return;

        float dx   = targetX - this.x;
        float dy   = targetY - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        float spawnX = this.x + (dist > 0 ? (float) ((dx / dist) * 16.0) : 0);
        float spawnY = this.y + (dist > 0 ? (float) ((dy / dist) * 16.0) : 0);

        board.addProjectile(new Projectile(spawnX, spawnY, targetX, targetY, this));
    }

    public void takeDamage(int damage) {
        this.status.hp -= damage;
        if (this.status.hp <= 0) {
            this.state = PlaneState.DEAD;
        }
    }

    public void setState(PlaneState state) {
        this.state = state;
    }

    public abstract boolean isRedTeam();
}