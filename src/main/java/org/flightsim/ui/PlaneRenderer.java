package org.flightsim.ui;

import org.flightsim.domain.Plane;
import org.flightsim.domain.PlaneState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.Random;

// Rysuje pojedynczy samolot na canvasie. Obrót wyliczamy z kierunku lotu,
// płomień z silnika losowo się zmienia żeby wyglądał żywiej.
public class PlaneRenderer {

    private static final double SIZE = 32.0;

    private final GameAssets assets;
    private final Random     random = new Random();

    public PlaneRenderer(GameAssets assets) {
        this.assets = assets;
    }

    public void draw(GraphicsContext gc, Plane p) {
        boolean isRed  = p.isRedTeam();
        boolean parked = p.getState() == PlaneState.PARKED;
        Image   jet    = assets.jet(isRed, parked);

        double px = p.x;
        double py = p.y;
        double angle = parked ? (isRed ? 90 : -90) : headingAngle(p, isRed);

        // zaparkowane samoloty rozkładamy w siatce żeby się nie nakładały
        if (parked) {
            px += (p.getId() % 4 - 1.5) * 16;
            py += (p.getId() / 4 % 4 - 1.5) * 16;
        }

        gc.save();
        gc.translate(px, py);
        gc.rotate(angle);

        if (!parked) drawFlame(gc);
        drawBody(gc, jet, isRed);

        gc.restore();
    }

    // Liczy kąt obrotu grafiki w stronę celu lub bazy
    private double headingAngle(Plane p, boolean isRed) {
        double targetX;
        double targetY;

        if (p.getState() == PlaneState.RETURNING_TO_BASE) {
            targetX = p.baseX;
            targetY = p.baseY;
        } else if (p.getTarget() != null) {
            targetX = p.getTarget().x;
            targetY = p.getTarget().y;
        } else {
            targetX = isRed ? 1000 : 0;
            targetY = p.y;
        }

        double dx = targetX - p.x;
        double dy = targetY - p.y;

        if (Math.abs(dx) > 0.1 || Math.abs(dy) > 0.1) {
            return Math.toDegrees(Math.atan2(dy, dx)) + 90;
        }
        return isRed ? 90 : -90;
    }

    // Płomień z dyszy — losowa długość żeby migotał
    private void drawFlame(GraphicsContext gc) {
        double flameLength = 10.0 + random.nextDouble() * 8.0;
        double flameWidth  = 6.0;

        gc.setFill(Color.ORANGE);
        gc.fillPolygon(
                new double[]{-flameWidth / 2, 0, flameWidth / 2},
                new double[]{SIZE / 2, SIZE / 2 + flameLength, SIZE / 2},
                3
        );

        gc.setFill(Color.YELLOW);
        gc.fillPolygon(
                new double[]{-flameWidth / 3, 0, flameWidth / 3},
                new double[]{SIZE / 2, SIZE / 2 + (flameLength * 0.6), SIZE / 2},
                3
        );
    }

    // Jak nie ma grafiki, rysujemy kolorowe kółko — lepsze niż crash
    private void drawBody(GraphicsContext gc, Image jet, boolean isRed) {
        if (jet == null || jet.isError()) {
            gc.setFill(isRed ? Color.RED : Color.BLUE);
            gc.fillOval(-6, -6, 12, 12);
        } else {
            gc.drawImage(jet, -SIZE / 2, -SIZE / 2, SIZE, SIZE);
        }
    }
}
