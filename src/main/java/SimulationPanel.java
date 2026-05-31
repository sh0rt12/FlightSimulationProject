import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class SimulationPanel extends Pane {

    private final Simulation simulation;
    private final Canvas canvas;
    private final GraphicsContext gc;

    private static final double SCALE_X = 1000.0 / 1000.0;
    private static final double SCALE_Y = 800.0 / 1000.0;

    public SimulationPanel(Simulation simulation) {
        this.simulation = simulation;
        this.canvas = new Canvas(1000, 800);
        this.gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
    }

    public void startLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            private final long nanoInterval = 100_000_000;

            @Override
            public void handle(long now) {

                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                if (now - lastUpdate >= nanoInterval) {
                    simulation.step();
                    lastUpdate = now;
                }

                draw();
            }
        };
        timer.start();
    }

    private void draw() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 1000, 800);

        drawBase(50 * SCALE_X, 500 * SCALE_Y, Color.RED, "RED AIRPORT");
        drawBase(950 * SCALE_X, 500 * SCALE_Y, Color.CORNFLOWERBLUE, "BLUE AIRPORT");

        List<Projectile> projectiles = simulation.getBoard().getProjectiles();
        for (Projectile proj : projectiles) {
            double px = proj.x * SCALE_X;
            double py = proj.y * SCALE_Y;

            Color bulletColor = (proj.getShooter() instanceof RedPlane) ? Color.ORANGE : Color.CYAN;

            gc.setFill(bulletColor);

            gc.fillOval(px - 3, py - 3, 6, 6);
        }


        List<Plane> planes = simulation.getBoard().getPlanes();
        for (Plane p : planes) {
            drawPlane(p);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(14));
        gc.fillText("Krok: " + simulation.getStepCount(), 10, 20);
    }

    private void drawBase(double x, double y, Color color, String name) {
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeRect(x - 20, y - 20, 40, 40);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(10));
        gc.fillText(name, x - 18, y - 25);
    }

    private void drawPlane(Plane p) {
        double x = p.x * SCALE_X;
        double y = p.y * SCALE_Y;

        Color color = (p instanceof RedPlane) ? Color.RED : Color.CORNFLOWERBLUE;

        gc.setFill(color);
        gc.fillOval(x - 8, y - 8, 16, 16);

        gc.setFill(Color.GRAY);
        gc.fillRect(x - 10, y - 15, 20, 3);
        gc.setFill(Color.GREEN);
        gc.fillRect(x - 10, y - 15, (p.hp / 3.0) * 20, 3);
    }
}