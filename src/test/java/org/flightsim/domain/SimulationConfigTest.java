package org.flightsim.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulationConfigTest {

    @Test
    void hasSensibleDefaults() {
        SimulationConfig config = new SimulationConfig();
        assertEquals(7, config.getInitialPlanesPerTeam());
        assertEquals(7, config.getTargetPlanesPerTeam());
    }

    @Test
    void settingInitialPlanesAlsoUpdatesTarget() {
        SimulationConfig config = new SimulationConfig();
        config.setInitialPlanesPerTeam(12);

        assertEquals(12, config.getInitialPlanesPerTeam());
        assertEquals(12, config.getTargetPlanesPerTeam());
    }

    @Test
    void settersRoundTrip() {
        SimulationConfig config = new SimulationConfig();
        config.setBaseSpeed(5.5f);
        config.setStartingHp(9);
        config.setStartingAmmo(40);
        config.setProjectileSpeed(20.0f);
        config.setFuelCapacity(250.0f);
        config.setFightRange(50.0f);

        assertEquals(5.5f, config.getBaseSpeed(), 1e-4);
        assertEquals(9, config.getStartingHp());
        assertEquals(40, config.getStartingAmmo());
        assertEquals(20.0f, config.getProjectileSpeed(), 1e-4);
        assertEquals(250.0f, config.getFuelCapacity(), 1e-4);
        assertEquals(50.0f, config.getFightRange(), 1e-4);
    }
}
