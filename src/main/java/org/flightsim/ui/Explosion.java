package org.flightsim.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// Rysuje animację wybuchu gdy samolot dostanie ostateczne trafienie.
// Wystarczyło 8 klatek żeby wyglądało ok, nie trzeba było więcej.
public class Explosion {

    private static final int    MAX_FRAMES = 8;
    private static final double MAX_RADIUS = 45.0;

    private final double x;
    private final double y;
    private int currentFrame = 0;

    public Explosion(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Rysuje klatkę i przesuwa licznik. Zwraca true gdy animacja się skończyła
    // — wtedy SimulationPanel usuwa ten obiekt z listy.
    public boolean drawAndAdvance(GraphicsContext gc) {
        double progress      = (double) currentFrame / MAX_FRAMES;
        double currentRadius = MAX_RADIUS * Math.sin(progress * Math.PI / 2);

        // zewnętrzna pomarańczowa "bańka" robi się coraz bardziej przeźroczysta
        gc.setFill(Color.rgb(255, 60, 0, 1.0 - progress));
        gc.fillOval(x - currentRadius, y - currentRadius, currentRadius * 2, currentRadius * 2);

        // wewnętrzne żółte jądro — trochę mniejsze dla efektu głębi
        gc.setFill(Color.rgb(255, 200, 0, 1.0 - progress));
        gc.fillOval(x - (currentRadius * 0.6), y - (currentRadius * 0.6), currentRadius * 1.2, currentRadius * 1.2);

        currentFrame++;
        return currentFrame >= MAX_FRAMES;
    }
}
