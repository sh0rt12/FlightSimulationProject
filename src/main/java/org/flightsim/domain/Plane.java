package org.flightsim.domain;

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

    private final PlaneMovement movement = new PlaneMovement(this);
    private final PlaneAI       ai       = new PlaneAI(this);

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

    public int getId() { return id; }
    public PlaneState getState() { return state; }
    public Plane getTarget() { return target; }
    public float getBaseSpeed() { return stats.baseSpeed; }
    public int   getMaxAmmo()   { return stats.maxAmmo; }
    public float getMaxFuel()   { return stats.maxFuel; }
    public int   getMaxHp()     { return stats.maxHp; }

    public void setCurrentSpeed(float v) { status.currentSpeed = v; }

    public float getVx() { return this.lastVx; }
    public float getVy() { return this.lastVy; }

    public void step(Board board) {
        ai.step(board);
    }

    public void move(Board board) {
        movement.move(board);
    }

    public void shoot(float targetX, float targetY, Board board) {
        if (this.status.ammo <= 0) return;

        float  dx   = targetX - this.x;
        float  dy   = targetY - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        float spawnX = this.x + (dist > 0 ? (float) ((dx / dist) * 16.0) : 0);
        float spawnY = this.y + (dist > 0 ? (float) ((dy / dist) * 16.0) : 0);

        float projSpeed = (board.getSimulation() != null)
                ? board.getSimulation().getConfig().getProjectileSpeed()
                : 15.0f;

        board.addProjectile(new Projectile(spawnX, spawnY, targetX, targetY, this, projSpeed));
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

    public abstract boolean isRedTeam();
}