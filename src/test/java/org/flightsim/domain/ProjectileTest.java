package org.flightsim.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectileTest {

    private final SimulationConfig config = new SimulationConfig();

    @Test
    void velocityPointsTowardTargetWithGivenSpeed() {
        Projectile p = new Projectile(0, 0, 10, 0, new RedPlane(1, 0, 0, config), 5);
        assertEquals(5.0f, p.getVx(), 1e-4);
        assertEquals(0.0f, p.getVy(), 1e-4);
    }

    @Test
    void zeroDistanceFallsBackToHorizontalVelocity() {
        Projectile p = new Projectile(100, 100, 100, 100, new RedPlane(1, 0, 0, config), 7);
        assertEquals(7.0f, p.getVx(), 1e-4);
        assertEquals(0.0f, p.getVy(), 1e-4);
    }

    @Test
    void moveAdvancesByVelocity() {
        Projectile p = new Projectile(0, 0, 10, 0, new RedPlane(1, 0, 0, config), 5);
        p.move();
        assertEquals(5.0f, p.x, 1e-4);
        assertEquals(0.0f, p.y, 1e-4);
    }

    @Test
    void isOutOfBoardDetectsBounds() {
        Projectile inside = new Projectile(500, 500, 600, 500, new RedPlane(1, 0, 0, config), 5);
        assertFalse(inside.isOutOfBoard(1000, 1000));

        Projectile offRight = new Projectile(1001, 500, 1002, 500, new RedPlane(1, 0, 0, config), 5);
        assertTrue(offRight.isOutOfBoard(1000, 1000));
    }

    @Test
    void rememberItsShooter() {
        RedPlane shooter = new RedPlane(1, 0, 0, config);
        Projectile p = new Projectile(0, 0, 10, 0, shooter, 5);
        assertSame(shooter, p.getShooter());
    }
}
