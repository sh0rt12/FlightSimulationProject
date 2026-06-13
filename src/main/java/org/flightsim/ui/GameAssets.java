package org.flightsim.ui;

import javafx.scene.image.Image;

/**
 * Ładuje i udostępnia grafiki symulacji (lotniska, samoloty, wiatr).
 * Brakujące pliki nie przerywają działania — odpowiednie obrazki pozostają null,
 * a kod rysujący korzysta wtedy z prostych kształtów zastępczych.
 */
public class GameAssets {

    private Image redAirport, blueAirport;
    private Image redJetFlying, redJetParked;
    private Image blueJetFlying, blueJetParked;
    private Image wind5Image, wind10Image, wind15Image;

    public GameAssets() {
        try {
            redAirport    = load("/RED AIRPORT.png");
            blueAirport   = load("/BLUE AIRPORT.png");

            redJetFlying  = load("/FIGHTER JET RED FLYING.png");
            redJetParked  = load("/FIGHTER JET RED PARKED.png");
            blueJetFlying = load("/FIGHTER JET BLUE FLYING.png");
            blueJetParked = load("/FIGHTER JET BLUE PARKED.png");

            wind5Image    = load("/WIND 5%.gif");
            wind10Image   = load("/WIND 10%.gif");
            wind15Image   = load("/WIND 15%.gif");

        } catch (Exception e) {
            System.out.println("Graphic loading error: " + e.getMessage());
        }
    }

    private Image load(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    public Image getRedAirport()  { return redAirport; }
    public Image getBlueAirport() { return blueAirport; }

    /** Sylwetka samolotu zależna od drużyny i tego, czy stoi w bazie. */
    public Image jet(boolean red, boolean parked) {
        if (parked) return red ? redJetParked : blueJetParked;
        return red ? redJetFlying : blueJetFlying;
    }

    /** Nakładka wiatru dla danego typu (1=5%, 2=10%, 3=15%); null gdy brak. */
    public Image windImage(int type) {
        return switch (type) {
            case 1  -> wind5Image;
            case 2  -> wind10Image;
            case 3  -> wind15Image;
            default -> null;
        };
    }
}
