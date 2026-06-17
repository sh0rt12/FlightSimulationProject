package org.flightsim.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaneMovementTest {

    private final SimulationConfig config = new SimulationConfig();

    @Test
    void redPlaneWithoutTargetDriftsTowardEnemySide() {
        Board board = new Board();
        RedPlane plane = new RedPlane(1, 500, 500, config);

        float startX = plane.x;
        plane.move(board);

        assertTrue(plane.x > startX, "Czerwony bez celu powinien leciec w prawo");
    }

    @Test
    void bluePlaneWithoutTargetDriftsTowardEnemySide() {
        Board board = new Board();
        BluePlane plane = new BluePlane(2, 500, 500, config);

        float startX = plane.x;
        plane.move(board);

        assertTrue(plane.x < startX, "Niebieski bez celu powinien leciec w lewo");
    }

    @Test
    void planeIsKeptInsideTheBoardBounds() {
        Board board = new Board();
        RedPlane plane = new RedPlane(1, 968, 500, config); // przy prawej krawedzi

        plane.move(board);

        assertTrue(plane.x <= 970.0f, "Pozycja nie może wyjsc poza prawą granicę 970");
    }
}
