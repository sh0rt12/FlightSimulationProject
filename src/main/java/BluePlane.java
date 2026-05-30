import java.util.Random;

public class BluePlane extends Plane {

    private static final Random random = new Random();
    private final float detectionRange;
    private final int evadeDuration;

    public BluePlane(int id, float x, float y) {
        super(id, x, y, 950.0f, 500.0f);
        this.maxFuel = 300.0f;
        this.fuel = 300.0f;
        this.maxAmmo = 10;
        this.ammo = 10;
        this.hp = 3;
        this.baseSpeed = 8.5f + random.nextFloat() * 2.0f;
        this.currentSpeed = this.baseSpeed;
        this.detectionRange = 80.0f + random.nextFloat() * 80.0f;
        this.evadeDuration = 3;
    }

    @Override
    public void step(Board board) {
        if (this.state == PlaneState.DEAD) return;

        this.target = board.getClosestEnemy(this);

        if (this.state == PlaneState.EVADING) {
            this.fuel -= 1.5f;
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
                this.fightTimer = 3;
            }
        } else if (this.state == PlaneState.FIGHTING) {
            this.fuel -= 1.5f;
            if (this.target != null) {
                shoot(this.target.x, this.target.y);
                this.ammo--;
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
}