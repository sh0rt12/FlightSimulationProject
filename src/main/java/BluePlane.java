import java.util.Random;

public class BluePlane extends Plane {

    private static final Random random = new Random();

    public BluePlane(int id, float x, float y, SimulationConfig config) {
        super(id, x, y,
              950.0f, 500.0f,
              buildStats(config),
              buildStatus(config));
    }

    private static PlaneStats buildStats(SimulationConfig config) {
        float speed = config.getBaseSpeed() + random.nextFloat() * config.getSpeedVariance();
        float range = config.getDetectionRangeMin()
                    + random.nextFloat() * (config.getDetectionRangeMax() - config.getDetectionRangeMin());
        return new PlaneStats(speed, config.getStartingHp(), config.getStartingAmmo(),
                              300.0f, range, config.getShotCooldown(), 3, 3);
    }

    private static PlaneStatus buildStatus(SimulationConfig config) {
        float speed = config.getBaseSpeed() + random.nextFloat() * config.getSpeedVariance();
        return new PlaneStatus(config.getStartingHp(), config.getStartingAmmo(),
                               180.0f, speed, 0);
    }

    @Override
    public boolean isRedTeam() { return false; }
}
