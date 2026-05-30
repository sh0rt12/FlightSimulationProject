import java.util.ArrayList;

public class Simulation {
    private Board board;
    private int stepCount;
    private int windLevel;
    private int totalRedPlanes;
    private int totalBluePlanes;
    private int totalShotsFired;
    private int redSpawnRate;
    private int blueSpawnRate;

    public Simulation() {
        this.board = new Board(1000, 1000);
        this.stepCount = 0;
        this.windLevel = 1;
        this.totalRedPlanes = 0;
        this.totalBluePlanes = 0;
        this.totalShotsFired = 0;
    }

    public void step() {
        this.stepCount++;

        applyWindEffect();

        for (Plane plane : new ArrayList<>(board.getPlanes())) {
            if (plane.state != PlaneState.DEAD) {
                plane.step(board);
                plane.move();
            }
        }

        for (Projectile projectile : new ArrayList<>(board.getProjectiles())) {
            projectile.move();
        }

        for (Airport airport : board.getAirports()) {
            airport.processTurn();
        }

        board.checkCollisions();

        board.getPlanes().removeIf(p -> p.state == PlaneState.DEAD);
        board.getProjectiles().removeIf(p -> p.isOutOfBoard(1000, 1000));

        spawnPlanes();
    }

    public void applyWindEffect() {

    }

    public void spawnPlanes() {

    }

    public Board getBoard() {
        return board;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void addTestPlane(Plane p) {
        this.board.addPlane(p);
        if (p instanceof RedPlane) totalRedPlanes++;
        if (p instanceof BluePlane) totalBluePlanes++;
    }

    public void printCurrentStatus() {
        System.out.println("====== KROK SYMULACJI: " + stepCount + " ======");
        for (Plane p : board.getPlanes()) {
            System.out.printf("Samolot [%s] ID:%d | Poz: (%.1f, %.1f) | Stan: %s | Paliwo: %.1f | Amunicja: %d | HP: %d%n",
                    p.getClass().getSimpleName(), p.id, p.x, p.y, p.state, p.fuel, p.ammo, p.hp);
        }
        System.out.println("====================================\n");
    }
}