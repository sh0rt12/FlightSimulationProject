package org.flightsim.domain;

/**
 * "Mózg" samolotu: maszyna stanów (lot, walka, unik, powrót do bazy),
 * zużycie paliwa, wybór najbliższego wroga i decyzje o oddaniu strzału.
 * Sam ruch w przestrzeni realizuje {@link PlaneMovement}.
 */
public class PlaneAI {

    // Paliwo: bazowe zużycie 0.5/krok daje ok. 20s lotu
    private static final float FUEL_BURN_BASE    = 0.5f;
    private static final float FUEL_BURN_FIGHT   = 1.0f;   // walka pali 2x szybciej
    private static final float FUEL_BURN_EVADE   = 1.4f;   // unik pali najszybciej
    private static final float FUEL_RETURN_RATIO = 0.18f;  // powrót do bazy przy 18% paliwa

    private final Plane plane;

    public PlaneAI(Plane plane) {
        this.plane = plane;
    }

    public void step(Board board) {
        if (plane.state == PlaneState.DEAD || plane.state == PlaneState.PARKED) return;

        if (plane.status.shotCooldown > 0) plane.status.shotCooldown--;

        burnFuel();

        if (plane.status.fuel <= 0) {
            plane.state = PlaneState.DEAD;
            return;
        }

        plane.target = board.getClosestEnemy(plane);

        if (shouldReturnToBase()) {
            plane.state = PlaneState.RETURNING_TO_BASE;
            plane.status.hitThisStep = false;
            return;
        }

        switch (plane.state) {
            case FLYING   -> stepFlying(board);
            case FIGHTING -> stepFighting(board);
            case EVADING  -> stepEvading();
            default       -> {}
        }

        plane.status.hitThisStep = false;
    }

    private void burnFuel() {
        float burn = switch (plane.state) {
            case FIGHTING -> FUEL_BURN_FIGHT;
            case EVADING  -> FUEL_BURN_EVADE;
            default       -> FUEL_BURN_BASE;
        };
        plane.status.fuel -= burn;
        if (plane.status.fuel < 0) plane.status.fuel = 0;
    }

    private boolean shouldReturnToBase() {
        if (plane.status.ammo <= 0) return true;
        if (plane.status.fuel <= plane.stats.maxFuel * FUEL_RETURN_RATIO) return true;
        if (plane.stats.maxHp > 1 && plane.status.hp == 1 && plane.state != PlaneState.EVADING) return true;
        return false;
    }

    private void stepFlying(Board board) {
        if (plane.target == null || plane.target.state == PlaneState.DEAD) return;

        double dist = distanceTo(plane.target);

        if (dist < plane.stats.fightRange) {
            plane.state             = PlaneState.FIGHTING;
            plane.status.fightTimer = 0;
            plane.status.orbitAngle = Math.atan2(plane.y - plane.target.y, plane.x - plane.target.x);
            return;
        }

        if (dist < plane.stats.detectionRange && plane.status.shotCooldown == 0) {
            plane.shoot(plane.target.x, plane.target.y, board);
        }
    }

    private void stepFighting(Board board) {
        plane.status.fightTimer++;

        if (plane.target == null || plane.target.state == PlaneState.DEAD) {
            plane.state = PlaneState.FLYING;
            return;
        }

        if (plane.status.hitThisStep && plane.status.hp <= plane.stats.evadeHpThreshold) {
            enterEvade();
            return;
        }

        if (plane.status.fightTimer >= plane.stats.fightDuration) {
            plane.state             = PlaneState.FLYING;
            plane.status.fightTimer = 0;
            return;
        }

        if (plane.status.shotCooldown == 0) {
            plane.shoot(plane.target.x, plane.target.y, board);
        }
    }

    private void stepEvading() {
        plane.status.evadeTimer++;

        if (plane.status.evadeTimer >= plane.stats.evadeDuration) {
            plane.status.evadeTimer = 0;
            plane.state = (plane.target != null
                    && plane.target.state != PlaneState.DEAD
                    && distanceTo(plane.target) < plane.stats.detectionRange)
                    ? PlaneState.FIGHTING
                    : PlaneState.FLYING;
            plane.status.fightTimer = 0;
        }
    }

    private void enterEvade() {
        plane.state             = PlaneState.EVADING;
        plane.status.evadeTimer = 0;
        plane.status.evadeDirection = (Math.random() < 0.5) ? 1.0f : -1.0f;
    }

    private double distanceTo(Plane other) {
        double dx = other.x - plane.x;
        double dy = other.y - plane.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
