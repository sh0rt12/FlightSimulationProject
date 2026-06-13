package org.flightsim.domain;

/** Liczy oddane strzały i trafienia obu drużyn oraz wynikającą z nich celność (%). */
public class BattleStats {

    private int redShots  = 0;
    private int redHits   = 0;
    private int blueShots = 0;
    private int blueHits  = 0;

    public void incrementRedShots()  { redShots++; }
    public void incrementRedHits()   { redHits++; }
    public void incrementBlueShots() { blueShots++; }
    public void incrementBlueHits()  { blueHits++; }

    public double getRedAccuracy() {
        if (redShots == 0) return 0.0;
        return ((double) redHits / redShots) * 100.0;
    }

    public double getBlueAccuracy() {
        if (blueShots == 0) return 0.0;
        return ((double) blueHits / blueShots) * 100.0;
    }
}
