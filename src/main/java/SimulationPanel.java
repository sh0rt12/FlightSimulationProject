import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimulationPanel extends Pane {

    private final Simulation simulation;
    private final Canvas canvas;
    private final GraphicsContext gc;

    private static final double SCALE_X = 1000.0 / 1000.0;
    private static final double SCALE_Y = 800.0 / 1000.0;

    private final List<Bullet> bullets = new ArrayList<>();

    private static class Bullet {
        double x, y, tx, ty;
        int life;

        Bullet(double x, double y, double tx, double ty) {
            this.x = x;
            this.y = y;
            this.tx = tx;
            this.ty = ty;
            this.life = 8;
        }
    }

    public SimulationPanel(Simulation simulation) {
        this.simulation = simulation;
        this.canvas = new Canvas(1000, 800);
        this.gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
    }

    public void startLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 200_000_000) {
                    spawnBullets();
                    simulation.step();
                    lastUpdate = now;
                }
                updateBullets();
                draw();
            }
        };
        timer.start();
    }

    private void spawnBullets() {
        for (Plane p : simulation.getBoard().getPlanes()) {
            if (p.state == PlaneState.FIGHTING && p.target != null && p.target.state != PlaneState.DEAD) {
                bullets.add(new Bullet(
                        p.x * SCALE_X,
                        p.y * SCALE_Y,
                        p.target.x * SCALE_X,
                        p.target.y * SCALE_Y
                ));
            }
        }
    }

    private void updateBullets() {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.life--;
            if (b.life <= 0) it.remove();
        }
    }

    private void draw() {
        gc.setFill(Color.rgb(10, 14, 26));
        gc.fillRect(0, 0, 1000, 800);

        drawBase(100 * SCALE_X, 350 * SCALE_Y, Color.RED, "BAZA R");
        drawBase(900 * SCALE_X, 550 * SCALE_Y, Color.CORNFLOWERBLUE, "BAZA B");

        for (Bullet b : bullets) {
            double alpha = b.life / 8.0;
            gc.setStroke(Color.color(1, 1, 1, alpha));
            gc.setLineWidth(1.5);
            gc.strokeLine(b.x, b.y, b.tx, b.ty);

            gc.setFill(Color.color(1, 1, 0.5, alpha));
            gc.fillOval(b.tx - 3, b.ty - 3, 6, 6);
        }

        List<Plane> planes = simulation.getBoard().getPlanes();
        for (Plane p : planes) {
            drawPlane(p);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(14));
        gc.fillText("Krok: " + simulation.getStepCount(), 10, 20);
    }

    private void drawPlane(Plane p) {
        double x = p.x * SCALE_X;
        double y = p.y * SCALE_Y;

        Color color = (p instanceof RedPlane) ? Color.RED : Color.CORNFLOWERBLUE;

        gc.setFill(color);
        gc.fillOval(x - 8, y - 8, 16, 16);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(10));
        gc.fillText("HP:" + p.hp, x + 10, y - 5);
        gc.fillText("F:" + (int) p.fuel, x + 10, y + 8);
        gc.fillText(p.state.toString(), x - 15, y - 12);
    }

    private void drawBase(double x, double y, Color color, String label) {
        gc.setStroke(color);
        gc.setLineWidth(1.5);
        gc.strokeOval(x - 25, y - 25, 50, 50);
        gc.setFill(color);
        gc.setFont(Font.font(11));
        gc.fillText(label, x - 18, y + 4);
    }
}