import java.util.Random;

public class BluePlane extends Plane {

    private static final Random random = new Random();

    public BluePlane(int id, float x, float y) {
        super(id, x, y, 950.0f, 500.0f, 80.0f + random.nextFloat() * 80.0f, 3, 3);
        this.maxFuel = 300.0f;
        this.fuel = 180.0f;
        this.maxAmmo = 10;
        this.ammo = 10;
        this.hp = 3;
        this.baseSpeed = 8.5f + random.nextFloat() * 2.0f;
        this.currentSpeed = this.baseSpeed;
    }
}