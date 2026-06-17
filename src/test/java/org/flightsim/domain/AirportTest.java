package org.flightsim.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirportTest {

    private final SimulationConfig config = new SimulationConfig();

    @Test
    void teamIsDecidedByPositionOnMap() {
        assertTrue(new Airport(50, 500, config).isRedAirport());
        assertFalse(new Airport(950, 500, config).isRedAirport());
    }

    @Test
    void dockingParksThePlaneAtTheAirport() {
        Airport airport = new Airport(50, 500, config);
        RedPlane plane = new RedPlane(1, 100, 600, config);

        airport.dockPlane(plane);

        assertEquals(PlaneState.PARKED, plane.getState());
        assertEquals(50.0f, plane.x, 1e-4);
        assertEquals(500.0f, plane.y, 1e-4);
        assertEquals(0.0f, plane.status.currentSpeed, 1e-4);
    }

    @Test
    void airportRefusesPlanesOverCapacity() {
        Airport airport = new Airport(50, 500, config);
        for (int i = 0; i < config.getAirportCapacity(); i++) {
            assertTrue(airport.canDock());
            airport.dockPlane(new RedPlane(i + 1, 100, 500, config));
        }
        assertFalse(airport.canDock());
    }

    @Test
    void planeIsRepairedAndRelaunchedAfterMaintenance() {
        Airport airport = new Airport(50, 500, config);
        RedPlane plane = new RedPlane(1, 100, 500, config);

        airport.dockPlane(plane);
        plane.status.hp   = 1;
        plane.status.ammo = 0;
        plane.status.fuel = 10.0f;

        for (int i = 0; i < config.getMaintenanceTime(); i++) {
            airport.processTurn();
        }

        assertEquals(PlaneState.FLYING, plane.getState());
        assertEquals(plane.getMaxHp(), plane.status.hp);
        assertEquals(plane.getMaxAmmo(), plane.status.ammo);
        assertEquals(plane.getMaxFuel(), plane.status.fuel, 1e-4);
        assertTrue(airport.canDock());
    }
}
