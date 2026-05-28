public class Simulation {
    // Atrybuty prywatne zgodne z diagramem klas
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

        for (Plane plane : board.getPlanes()) {
            plane.step(board);
            plane.move();
        }

        for (Projectile projectile : board.getProjectiles()) {
            projectile.move();
        }

        // 4. Lotniska przetwarzają swoją turę (np. tankują zaparkowane samoloty)
        for (Airport airport : board.getAirports()) {
            airport.processTurn();
        }

        board.checkCollisions();

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

    // Pozwala dodać samolot bezpośrednio z poziomu Main
    public void addTestPlane(Plane p) {
        this.board.addPlane(p);
        if (p instanceof RedPlane) totalRedPlanes++;
        if (p instanceof BluePlane) totalBluePlanes++;
    }

    // Pozwala wypisać aktualny stan obiektów w konsoli
    public void printCurrentStatus() {
        System.out.println("====== KROK SYMULACJI: " + stepCount + " ======");
        for (Plane p : board.getPlanes()) {
            System.out.printf("Samolot [%s] ID:%d | Poz: (%.1f, %.1f) | Stan: %s | Paliwo: %.1f | Amunicja: %d | HP: %d%n",
                    p.getClass().getSimpleName(), p.id, p.x, p.y, p.state, p.fuel, p.ammo, p.hp);
        }
        System.out.println("====================================\n");
    }
}