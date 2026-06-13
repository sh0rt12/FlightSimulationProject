package org.flightsim.domain;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * System pogody: losowo pojawiający się i zanikający wiatr o określonym typie
 * (sile) i kierunku. Wpływa na prędkość samolotów przez {@link #getWindMultiplier}.
 */
public class WeatherSystem {

    private final SimulationConfig config;
    private final Random           random = new Random();

    private boolean active        = false;
    private int     turnsLeft     = 0;
    private int     windType      = 1;
    private int     windDirection = 0;

    public WeatherSystem(SimulationConfig config) {
        this.config = config;
    }

    public void update(int stepCount) {
        if (active) {
            turnsLeft--;
            if (turnsLeft <= 0) {
                active = false;
                System.out.println("Wind died down (step: " + stepCount + ")");
            }
        } else if (random.nextInt(config.getWindSpawnChance()) == 0) {
            active        = true;
            turnsLeft     = config.getWindDuration();
            windType      = ThreadLocalRandom.current().nextInt(1, 4);
            windDirection = ThreadLocalRandom.current().nextInt(0, 4);
            System.out.println("Wind appeared! Type: " + windType
                    + ", Direction: " + windDirection + " (step: " + stepCount + ")");
        }
    }

    public double getWindMultiplier(Plane p) {
        if (!active) return 1.0;

        double penalty = switch (windType) {
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

    public boolean isActive()         { return active; }
    public int     getWindType()      { return windType; }
    public int     getWindDirection() { return windDirection; }
}
