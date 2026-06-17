package org.flightsim.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BattleStatsTest {

    @Test
    void accuracyIsZeroWhenNoShotsFired() {
        BattleStats stats = new BattleStats();
        assertEquals(0.0, stats.getRedAccuracy(), 1e-9);
        assertEquals(0.0, stats.getBlueAccuracy(), 1e-9);
    }

    @Test
    void redAccuracyIsHitsOverShotsAsPercent() {
        BattleStats stats = new BattleStats();
        for (int i = 0; i < 4; i++) stats.incrementRedShots();
        stats.incrementRedHits();

        assertEquals(25.0, stats.getRedAccuracy(), 1e-9);
    }

    @Test
    void teamsAreCountedIndependently() {
        BattleStats stats = new BattleStats();
        stats.incrementRedShots();
        stats.incrementRedHits();
        stats.incrementBlueShots();
        stats.incrementBlueShots();

        assertEquals(100.0, stats.getRedAccuracy(), 1e-9);
        assertEquals(0.0, stats.getBlueAccuracy(), 1e-9);
    }
}
