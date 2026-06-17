package org.flightsim.domain;

import java.util.Random;

// Konkretna implementacja samolotu dla drużyny czerwonych.
// Różni się od BluePlane tylko pozycją startową i zwracaną flagą isRedTeam().
public class RedPlane extends Plane {

    private static final Random random = new Random();

    // startuje przy lewym lotnisku (x=100), baza przy x=50
    public RedPlane(int id, float x, float y, SimulationConfig config) {
        super(id, x, y, 50.0f, 500.0f, buildStats(config), buildStatus(config));
    }

    // Lekkie losowanie prędkości i zasięgu — każdy samolot trochę inny
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
    public boolean isRedTeam() { return true; }
}
