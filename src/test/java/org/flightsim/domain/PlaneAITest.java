package org.flightsim.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaneAITest {

    private final SimulationConfig config = new SimulationConfig();

    @Test
    void planeOutOfAmmoHeadsBackToBase() {
        Board board = new Board();
        RedPlane plane = new RedPlane(1, 500, 500, config);
        plane.status.ammo = 0;
        plane.status.fuel = plane.getMaxFuel();   // paliwo full

        plane.step(board);

        assertEquals(PlaneState.RETURNING_TO_BASE, plane.getState());
    }

    @Test
    void planeRunningOutOfFuelDies() {
        Board board = new Board();
        RedPlane plane = new RedPlane(1, 500, 500, config);
        plane.status.fuel = 0.4f;                 // mniej niz jedno spalanie na krok

        plane.step(board);

        assertEquals(PlaneState.DEAD, plane.getState());
    }

    @Test
    void planeEntersFightWhenEnemyIsWithinFightRange() {
        Board board = new Board();
        RedPlane  red  = new RedPlane(1, 500, 500, config);
        BluePlane blue = new BluePlane(2, 510, 500, config); // w zasiegu walki
        board.addPlane(red);
        board.addPlane(blue);

        red.step(board);

        assertEquals(PlaneState.FIGHTING, red.getState());
    }
}
