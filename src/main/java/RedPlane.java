public class RedPlane extends Plane {

    public RedPlane(int id, float x, float y) {
        super(id, x, y);
        this.maxFuel = 100.0f;
        this.fuel = 100.0f;
        this.maxAmmo = 10;
        this.ammo = 10;
        this.hp = 3;
        this.baseSpeed = 9.5f;
        this.currentSpeed = 9.5f;
    }

    @Override
    public void step(Board board) {
        // mechanika decyzji w kroku tu bedzie
    }
}