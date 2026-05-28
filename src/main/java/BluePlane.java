public class BluePlane extends Plane {

    public BluePlane(int id, float x, float y) {
        super(id, x, y);
        this.maxFuel = 80.0f;
        this.fuel = 80.0f;
        this.maxAmmo = 12;
        this.ammo = 12;
        this.hp = 2;
        this.baseSpeed = 8.5f;
        this.currentSpeed = 8.5f;
    }

    @Override
    public void step(Board board) {

    }
}