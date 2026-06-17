package org.flightsim.domain;

import java.util.Random;

// Samolot drużyny niebieskich — startuje przy prawym lotnisku (x=900), baza przy x=950.
// Logika identyczna z RedPlane, różni się tylko pozycją i flagą drużyny.
public class BluePlane extends Plane {

    private static final Random random = new Random();

    public BluePlane(int id, float x, float y, SimulationConfig config) {
        super(id, x, y, 950.0f, 500.0f, buildStats(config), buildStatus(config));
    }

    private static PlaneStats buildStats(SimulationConfig config) {
        float speed = config.getBaseSpeed() + random.nextFloat() * config.getSpeedVariance();
        float range = config.getDetectionRangeMin()
                    + random.nextFloat() * (config.getDetectionRangeMax() - config.getDetectionRangeMin());
        return new PlaneStats(speed, config.getStartingHp(), config.getStartingAmmo(),
                              config.getFuelCapacity(), range, config.getFightRange(), config.getShotCooldown(),
                              config.getEvadeDuration(), config.getFightDuration(),
                              config.getEvadeHpThreshold());
    }

    private static PlaneStatus buildStatus(SimulationConfig config) {
        float speed = config.getBaseSpeed() + random.nextFloat() * config.getSpeedVariance();
        return new PlaneStatus(config.getStartingHp(), config.getStartingAmmo(),
                               config.getFuelCapacity(), speed, 0);
    }

    @Override
    public boolean isRedTeam() { return false; }
}
