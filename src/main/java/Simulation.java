import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation {
    private Board board;
    private int stepCount;
    private int windLevel;
    private int totalRedPlanes;
    private int totalBluePlanes;
    private int totalShotsFired;

    private boolean weatherActive = false;
    private int weatherTurnsLeft = 0;
    private int currentWindType = 1;
    private final Random random = new Random();

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

        if (weatherActive) {
            weatherTurnsLeft--;
            if (weatherTurnsLeft <= 0) {
                weatherActive = false;
                System.out.println("--- Wiatr ustal (Krok: " + stepCount + ") ---");
            }
        } else {
            if (random.nextInt(50) == 0) {
                weatherActive = true;
                weatherTurnsLeft = 25;
                currentWindType = ThreadLocalRandom.current().nextInt(1, 4);
                System.out.println(">>> POJAWIŁ SIĘ WIATR! Typ: wiatr" + currentWindType + ".png (Krok: " + stepCount + ")");
            }
        }

        applyWindEffect();

        for (Plane plane : new ArrayList<>(board.getPlanes())) {
            if (plane.state != PlaneState.DEAD) {
                plane.step(board);
                plane.move(board);
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

    public boolean isWeatherActive() {
        return weatherActive;
    }

    public int getCurrentWindType() {
        return currentWindType;
    }

    public void applyWindEffect() {

    }

    public void spawnPlanes() {
        int currentRed = 0;
        int currentBlue = 0;

        for (Plane p : board.getPlanes()) {
            if (p instanceof RedPlane) currentRed++;
            if (p instanceof BluePlane) currentBlue++;
        }

        int targetCount = 5;

        if (currentRed < targetCount) {
            this.totalRedPlanes++;
            float randomY = 50.0f + random.nextFloat() * 900.0f;

            RedPlane newRed = new RedPlane(this.totalRedPlanes * 2 - 1, 100.0f, randomY);
            newRed.setState(PlaneState.FLYING);
            board.addPlane(newRed);
        }

        if (currentBlue < targetCount) {
            this.totalBluePlanes++;
            float randomY = 50.0f + random.nextFloat() * 900.0f;

            BluePlane newBlue = new BluePlane(this.totalBluePlanes * 2, 900.0f, randomY);
            newBlue.setState(PlaneState.FLYING);
            board.addPlane(newBlue);
        }
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
}