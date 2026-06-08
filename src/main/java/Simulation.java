import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Simulation {
    private final Board            board;
    private final SimulationConfig config;
    private final Random           random = new Random();

    private int stepCount;

    private boolean weatherActive   = false;
    private int     weatherTurnsLeft = 0;
    private int     currentWindType  = 1;
    private int     windDirection    = 0;

    private int totalRedPlanes  = 0;
    private int totalBluePlanes = 0;

    public Simulation(SimulationConfig config) {
        this.config = config;
        this.board  = new Board(1000, 1000);
        this.board.setSimulation(this);
        this.board.getAirports().add(new Airport(50.0f,  500.0f, config));
        this.board.getAirports().add(new Airport(950.0f, 500.0f, config));
        this.stepCount = 0;
    }

    public void step() {
        this.stepCount++;

        updateWeather();

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

    private void updateWeather() {
        if (weatherActive) {
            weatherTurnsLeft--;
            if (weatherTurnsLeft <= 0) {
                weatherActive = false;
                System.out.println("Wind died down (step: " + stepCount + ")");
            }
        } else if (random.nextInt(config.getWindSpawnChance()) == 0) {
            weatherActive    = true;
            weatherTurnsLeft = config.getWindDuration();
            currentWindType  = ThreadLocalRandom.current().nextInt(1, 4);
            windDirection    = ThreadLocalRandom.current().nextInt(0, 4);
            System.out.println("Wind appeared! Type: " + currentWindType
                    + ", Direction: " + windDirection + " (step: " + stepCount + ")");
        }
    }

    private void spawnPlanes() {
        int currentRed  = 0;
        int currentBlue = 0;

        for (Plane p : board.getPlanes()) {
            if (p.isRedTeam()) currentRed++;
            else               currentBlue++;
        }

        int target = config.getTargetPlanesPerTeam();

        if (currentRed < target) {
            totalRedPlanes++;
            float y = 50.0f + random.nextFloat() * 900.0f;
            RedPlane newRed = new RedPlane(totalRedPlanes * 2 - 1, 100.0f, y, config);
            newRed.setState(PlaneState.FLYING);
            board.addPlane(newRed);
        }

        if (currentBlue < target) {
            totalBluePlanes++;
            float y = 50.0f + random.nextFloat() * 900.0f;
            BluePlane newBlue = new BluePlane(totalBluePlanes * 2, 900.0f, y, config);
            newBlue.setState(PlaneState.FLYING);
            board.addPlane(newBlue);
        }
    }

    public double getWindMultiplier(Plane p) {
        if (!weatherActive) return 1.0;

        double penalty = switch (currentWindType) {
            case 2  -> 0.10;
            case 3  -> 0.15;
            default -> 0.05;
        };

        boolean flyingWithWind =
                (windDirection == 0 && p.getVy() < 0) ||
                (windDirection == 1 && p.getVy() > 0) ||
                (windDirection == 2 && p.getVx() > 0) ||
                (windDirection == 3 && p.getVx() < 0);

        return flyingWithWind ? 1.0 + penalty : 1.0 - penalty;
    }

    public void addTestPlane(Plane p) {
        board.addPlane(p);
        if (p.isRedTeam()) totalRedPlanes++;
        else               totalBluePlanes++;
    }

    // Gettery
    public Board            getBoard()            { return board; }
    public SimulationConfig getConfig()           { return config; }
    public int              getStepCount()        { return stepCount; }
    public boolean          isWeatherActive()     { return weatherActive; }
    public int              getCurrentWindType()  { return currentWindType; }
    public int              getWindDirection()    { return windDirection; }
}
