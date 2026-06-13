package org.flightsim.domain;

public class Projectile {
    public float x;
    public float y;
    private float vx;
    private float vy;
    private Plane shooter;

    public Projectile(float x, float y, float targetX, float targetY, Plane shooter, float speed) {
        this.x       = x;
        this.y       = y;
        this.shooter = shooter;

        float  dx       = targetX - x;
        float  dy       = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            this.vx = (float) ((dx / distance) * speed);
            this.vy = (float) ((dy / distance) * speed);
        } else {
            this.vx = speed;
            this.vy = 0;
        }
    }

    public void move() {
        this.x += this.vx;
        this.y += this.vy;
    }

    public boolean isOutOfBoard(int width, int height) {
        return this.x < 0 || this.x > width || this.y < 0 || this.y > height;
    }

    public Plane getShooter() { return shooter; }
    public float getVx()      { return vx; }
    public float getVy()      { return vy; }
}
