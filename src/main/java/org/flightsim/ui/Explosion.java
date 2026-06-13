package org.flightsim.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** Krótka animacja wybuchu rysowana w miejscu zestrzelonego samolotu. */
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

    /**
     * Rysuje bieżącą klatkę wybuchu i przechodzi do następnej.
     * @return true, gdy animacja dobiegła końca (można usunąć wybuch).
     */
    public boolean drawAndAdvance(GraphicsContext gc) {
        double progress      = (double) currentFrame / MAX_FRAMES;
        double currentRadius = MAX_RADIUS * Math.sin(progress * Math.PI / 2);

        gc.setFill(Color.rgb(255, 60, 0, 1.0 - progress));
        gc.fillOval(x - currentRadius, y - currentRadius, currentRadius * 2, currentRadius * 2);

        gc.setFill(Color.rgb(255, 200, 0, 1.0 - progress));
        gc.fillOval(x - (currentRadius * 0.6), y - (currentRadius * 0.6), currentRadius * 1.2, currentRadius * 1.2);

        currentFrame++;
        return currentFrame >= MAX_FRAMES;
    }
}
