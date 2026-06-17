package org.flightsim.ui;

import javafx.scene.image.Image;

// Ładuje wszystkie grafiki przy starcie. Jak brakuje jakiegoś pliku PNG/GIF,
// kod rysujący automatycznie przełącza się na zastępcze kształty — gra nadal działa.
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

            // animowane GIFy wiatru — 3 poziomy siły
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

    // Zwraca odpowiednią grafikę samolotu — zależy od drużyny i czy stoi w bazie
    public Image jet(boolean red, boolean parked) {
        if (parked) return red ? redJetParked : blueJetParked;
        return red ? redJetFlying : blueJetFlying;
    }

    // type: 1=słaby, 2=średni, 3=silny wiatr; null gdy nieznany
    public Image windImage(int type) {
        return switch (type) {
            case 1  -> wind5Image;
            case 2  -> wind10Image;
            case 3  -> wind15Image;
            default -> null;
        };
    }
}
