package org.flightsim.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaneTest {

    private final SimulationConfig config = new SimulationConfig();

    @Test
    void teamMembershipMatchesSubclass() {
        assertTrue(new RedPlane(1, 0, 0, config).isRedTeam());
        assertFalse(new BluePlane(2, 0, 0, config).isRedTeam());
    }

    @Test
    void takeDamageReducesHp() {
        RedPlane plane = new RedPlane(1, 0, 0, config);
        int hpBefore = plane.status.hp;

        plane.takeDamage(1);

        assertEquals(hpBefore - 1, plane.status.hp);
    }

    @Test
    void planeDiesWhenHpReachesZero() {
        RedPlane plane = new RedPlane(1, 0, 0, config);

        plane.takeDamage(plane.getMaxHp());

        assertEquals(PlaneState.DEAD, plane.getState());
    }

    @Test
    void shootingSpawnsProjectileAndSpendsAmmo() {
        Board board = new Board();
        RedPlane plane = new RedPlane(1, 0, 0, config);
        int ammoBefore = plane.status.ammo;

        plane.shoot(100, 0, board);

        assertEquals(1, board.getProjectiles().size());
        assertEquals(ammoBefore - 1, plane.status.ammo);
    }

    @Test
    void cannotShootWithoutAmmo() {
        Board board = new Board();
        RedPlane plane = new RedPlane(1, 0, 0, config);
        plane.status.ammo = 0;

        plane.shoot(100, 0, board);

        assertTrue(board.getProjectiles().isEmpty());
        assertEquals(0, plane.status.ammo);
    }
}
