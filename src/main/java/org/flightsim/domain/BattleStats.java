package org.flightsim.domain;

// Prosta klasa do zbierania statystyk strzałów — oddzielona od Simulation
// żeby nie zaśmiecać głównego silnika licznikami.
public class BattleStats {

    private int redShots  = 0;
    private int redHits   = 0;
    private int blueShots = 0;
    private int blueHits  = 0;

    public void incrementRedShots()  { redShots++; }
    public void incrementRedHits()   { redHits++; }
    public void incrementBlueShots() { blueShots++; }
    public void incrementBlueHits()  { blueHits++; }

    // Zwraca 0.0 zanim ktokolwiek wystrzelił żeby nie było dzielenia przez zero
    public double getRedAccuracy() {
        if (redShots == 0) return 0.0;
        return ((double) redHits / redShots) * 100.0;
    }

    public double getBlueAccuracy() {
        if (blueShots == 0) return 0.0;
        return ((double) blueHits / blueShots) * 100.0;
    }
}
