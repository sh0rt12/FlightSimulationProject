public abstract class Plane {
    protected final int id;
    public float x;
    public float y;
    public final float baseX;
    public final float baseY;

    protected final PlaneStats  stats;
    protected final PlaneStatus status;

    protected Plane      target;
    protected PlaneState state;

    protected float lastVx = 0;
    protected float lastVy = 0;

    public Plane(int id, float x, float y, float baseX, float baseY,
                 PlaneStats stats, PlaneStatus status) {
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

    //Logika stanów
    public void step(Board board) {
        if (this.state == PlaneState.DEAD || this.state == PlaneState.PARKED) return;

        if (this.status.shotCooldown > 0) this.status.shotCooldown--;

        this.target = board.getClosestEnemy(this);

        if (shouldReturnToBase()) {
            this.state = PlaneState.RETURNING_TO_BASE;
            this.status.hitThisStep = false;
            return;
        }

        switch (this.state) {
            case FLYING            -> stepFlying(board);
            case FIGHTING          -> stepFighting(board);
            case EVADING           -> stepEvading();
            case RETURNING_TO_BASE -> stepReturning();
            default                -> {}
        }

        this.status.hitThisStep = false;
    }

    private boolean shouldReturnToBase() {
        if (this.status.ammo <= 0) return true;
        if (this.status.hp == 1 && this.state != PlaneState.EVADING) return true;
        return false;
    }

    private void stepFlying(Board board) {
        if (this.target == null || this.target.state == PlaneState.DEAD) return;

        double dist = distanceTo(this.target);


        if (dist < this.stats.fightRange) {
            this.state             = PlaneState.FIGHTING;
            this.status.fightTimer = 0;
            this.status.orbitAngle = Math.atan2(this.y - this.target.y, this.x - this.target.x);
            return;
        }


        if (dist < this.stats.detectionRange && this.status.shotCooldown == 0) {
            shoot(this.target.x, this.target.y, board);
        }
    }

    private void stepFighting(Board board) {
        this.status.fightTimer++;

        if (this.target == null || this.target.state == PlaneState.DEAD) {
            this.state = PlaneState.FLYING;
            return;
        }


        if (this.status.hitThisStep && this.status.hp <= this.stats.evadeHpThreshold) {
            enterEvade();
            return;
        }


        if (this.status.fightTimer >= this.stats.fightDuration) {
            this.state             = PlaneState.FLYING;
            this.status.fightTimer = 0;
            return;
        }

        if (this.status.shotCooldown == 0) {
            shoot(this.target.x, this.target.y, board);
        }
    }

    private void stepEvading() {
        this.status.evadeTimer++;

        if (this.status.evadeTimer >= this.stats.evadeDuration) {
            this.status.evadeTimer = 0;
            this.state = (this.target != null
                    && this.target.state != PlaneState.DEAD
                    && distanceTo(this.target) < this.stats.detectionRange)
                    ? PlaneState.FIGHTING
                    : PlaneState.FLYING;
            this.status.fightTimer = 0;
        }
    }

    private void stepReturning() {
        if (this.status.hp == this.stats.maxHp && this.status.ammo > 0) {
            this.state = PlaneState.FLYING;
        }
    }

    private void enterEvade() {
        this.state             = PlaneState.EVADING;
        this.status.evadeTimer = 0;
        this.status.evadeDirection = (Math.random() < 0.5) ? 1.0f : -1.0f;
    }

    //logika ruchu w stanach
    public void move(Board board) {
        if (this.state == PlaneState.DEAD || this.state == PlaneState.PARKED) return;

        float moveX = 0;
        float moveY = 0;

        switch (this.state) {
            case RETURNING_TO_BASE -> {
                float  dx       = this.baseX - this.x;
                float  dy       = this.baseY - this.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < 15.0) {
                    Airport airport = board.getAirportFor(this);
                    if (airport != null && airport.canDock()) {
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
            }
            case FIGHTING -> {
                if (this.target != null && this.target.state != PlaneState.DEAD) {
                    float[] orbit = orbitMove();
                    moveX = orbit[0];
                    moveY = orbit[1];
                }
            }
            case EVADING -> {
                if (this.target != null && this.target.state != PlaneState.DEAD) {
                    float  dx       = this.target.x - this.x;
                    float  dy       = this.target.y - this.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance > 0) {
                        float normX = (float) (dx / distance);
                        float normY = (float) (dy / distance);

                        float progress       = (float) this.status.evadeTimer / this.stats.evadeDuration;
                        float evasionStrength = (float) Math.sin(progress * Math.PI);
                        moveX = -normY * this.status.evadeDirection * evasionStrength
                                + normX * (1.0f - evasionStrength * 0.5f);
                        moveY =  normX * this.status.evadeDirection * evasionStrength
                                + normY * (1.0f - evasionStrength * 0.5f);
                        double len = Math.sqrt(moveX * moveX + moveY * moveY);
                        if (len > 0) { moveX /= len; moveY /= len; }
                    }
                } else {
                    this.state = PlaneState.RETURNING_TO_BASE;
                }
            }
            default -> { //flying

                if (this.target != null && this.target.state != PlaneState.DEAD) {
                    float  dx       = this.target.x - this.x;
                    float  dy       = this.target.y - this.y;
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
        }

        // separacja — odpychanie od innych samolotów w pobliżu
        float sepX = 0, sepY = 0;
        final float SEP_RADIUS = 24.0f;

        for (Plane other : board.getPlanes()) {
            if (other == this || other.state == PlaneState.DEAD || other.state == PlaneState.PARKED) continue;
            float dx   = this.x - other.x;
            float dy   = this.y - other.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist > 0 && dist < SEP_RADIUS) {

                float strength = (SEP_RADIUS - dist) / SEP_RADIUS;
                strength *= strength;
                sepX += (dx / dist) * strength;
                sepY += (dy / dist) * strength;
            }
        }

        // separacja i ruch pomieszane dla plynnosci
        float sepLen = (float) Math.sqrt(sepX * sepX + sepY * sepY);
        if (sepLen > 0) {
            moveX = moveX * 0.6f + (sepX / sepLen) * 0.4f;
            moveY = moveY * 0.6f + (sepY / sepLen) * 0.4f;
            float len = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            if (len > 0) { moveX /= len; moveY /= len; }
        }

        this.lastVx = moveX;
        this.lastVy = moveY;

        double windMod = (board.getSimulation() != null)
                ? board.getSimulation().getWindMultiplier(this)
                : 1.0;

        //predkosc w zaleznosci od stanu
        float speedMod = switch (this.state) {
            case FIGHTING -> 0.75f;
            case EVADING  -> 1.3f;
            default       -> 1.0f;
        };

        this.x += (float) (moveX * this.status.currentSpeed * speedMod * windMod);
        this.y += (float) (moveY * this.status.currentSpeed * speedMod * windMod);
    }

    // system walki - samoloty sie okrazaja
    private float[] orbitMove() {
        double orbitRadius  = this.stats.fightRange * 0.8;
        double anglePerStep = 0.08;

        this.status.orbitAngle += anglePerStep;

        double targetX = this.target.x + Math.cos(this.status.orbitAngle) * orbitRadius;
        double targetY = this.target.y + Math.sin(this.status.orbitAngle) * orbitRadius;

        double dx   = targetX - this.x;
        double dy   = targetY - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            return new float[]{ (float) (dx / dist), (float) (dy / dist) };
        }
        return new float[]{ 0, 0 };
    }

    private void tryEscapeOffMap() {
        float  dx       = this.baseX - this.x;
        float  dy       = this.baseY - this.y;
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

        float  dx   = targetX - this.x;
        float  dy   = targetY - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        float spawnX = this.x + (dist > 0 ? (float) ((dx / dist) * 16.0) : 0);
        float spawnY = this.y + (dist > 0 ? (float) ((dy / dist) * 16.0) : 0);

        board.addProjectile(new Projectile(spawnX, spawnY, targetX, targetY, this));
        this.status.shotCooldown = this.stats.shotCooldown;
        this.status.ammo--;
    }

    public void takeDamage(int damage) {
        this.status.hp         -= damage;
        this.status.hitThisStep = true;
        if (this.status.hp <= 0) {
            this.state = PlaneState.DEAD;
        }
    }

    public void setState(PlaneState state) {
        this.state = state;
    }

    private double distanceTo(Plane other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public abstract boolean isRedTeam();
}