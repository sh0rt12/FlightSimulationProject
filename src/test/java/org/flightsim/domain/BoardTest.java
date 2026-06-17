package org.flightsim.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    private final SimulationConfig config = new SimulationConfig();

    @Test
    void closestEnemyPicksTheNearestOpponent() {
        Board board = new Board();
        RedPlane  red   = new RedPlane(1, 100, 500, config);
        BluePlane near  = new BluePlane(2, 200, 500, config); // dystans 100
        BluePlane far   = new BluePlane(4, 600, 500, config); // dystans 500
        board.addPlane(red);
        board.addPlane(near);
        board.addPlane(far);

        assertSame(near, board.getClosestEnemy(red));
    }

    @Test
    void closestEnemyIgnoresOwnTeam() {
        Board board = new Board();
        BluePlane blue = new BluePlane(2, 200, 500, config);
        RedPlane  red  = new RedPlane(1, 100, 500, config);
        board.addPlane(blue);
        board.addPlane(red);

        assertSame(red, board.getClosestEnemy(blue));
    }

    @Test
    void airportForReturnsMatchingTeamBase() {
        Board board = new Board();
        board.getAirports().add(new Airport(50, 500, config));
        board.getAirports().add(new Airport(950, 500, config));

        assertTrue(board.getAirportFor(new RedPlane(1, 100, 500, config)).isRedAirport());
        assertFalse(board.getAirportFor(new BluePlane(2, 900, 500, config)).isRedAirport());
    }

    @Test
    void projectileHittingEnemyDealsDamageAndIsRemoved() {
        Board board = new Board();
        BluePlane target  = new BluePlane(2, 100, 100, config);
        RedPlane  shooter = new RedPlane(1, 0, 0, config);
        board.addPlane(target);

        int hpBefore = target.status.hp;
        board.addProjectile(new Projectile(100, 100, 100, 100, shooter, 5)); // trafia w cel
        board.checkCollisions();

        assertEquals(hpBefore - 1, target.status.hp);
        assertTrue(board.getProjectiles().isEmpty());
    }

    @Test
    void friendlyFireIsIgnored() {
        Board board = new Board();
        RedPlane ally    = new RedPlane(3, 100, 100, config);
        RedPlane shooter = new RedPlane(1, 0, 0, config);
        board.addPlane(ally);

        int hpBefore = ally.status.hp;
        board.addProjectile(new Projectile(100, 100, 100, 100, shooter, 5));
        board.checkCollisions();

        assertEquals(hpBefore, ally.status.hp);              // brak obrazen
        assertFalse(board.getProjectiles().isEmpty());       // pocisk nie znika
    }
}
