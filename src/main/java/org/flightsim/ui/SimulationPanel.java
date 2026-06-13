package org.flightsim.ui;

import org.flightsim.domain.Plane;
import org.flightsim.domain.Projectile;
import org.flightsim.domain.Simulation;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;

import java.util.List;
import java.util.ArrayList;

/**
 * Płótno symulacji: pętla animacji oraz rysowanie sceny (niebo, wiatr, lotniska,
 * pociski, samoloty, wybuchy). Szczegóły rysowania samolotów i grafiki są
 * delegowane do {@link PlaneRenderer} i {@link GameAssets}.
 */
public class SimulationPanel extends Pane {

    private static final double SCALE_X = 1.0;
    private static final double SCALE_Y = 1.0;

    private final Simulation      simulation;
    private final Canvas          canvas;
    private final GraphicsContext gc;

    private final GameAssets       assets        = new GameAssets();
    private final PlaneRenderer    planeRenderer = new PlaneRenderer(assets);
    private final List<Explosion>  explosions    = new ArrayList<>();

    public SimulationPanel(Simulation simulation) {
        this.simulation = simulation;
        this.canvas = new Canvas(1000, 1000);
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
                    List<Plane> planesBefore = new ArrayList<>(simulation.getBoard().getPlanes());

                    simulation.step();

                    for (Plane p : planesBefore) {
                        if (!simulation.getBoard().getPlanes().contains(p)) {
                            explosions.add(new Explosion(p.x * SCALE_X, p.y * SCALE_Y));
                        }
                    }

                    lastUpdate = now;
                }

                draw();
            }
        };
        timer.start();
    }

    private void draw() {
        gc.clearRect(0, 0, 1000, 1000);

        drawSky();
        drawWind();
        drawAirports();
        drawProjectiles();

        for (Plane p : simulation.getBoard().getPlanes()) {
            planeRenderer.draw(gc, p);
        }

        explosions.removeIf(exp -> exp.drawAndAdvance(gc));
    }

    private void drawSky() {
        try {
            RadialGradient skyGradient = new RadialGradient(
                    0, 0, 500, 500, 700, false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.rgb(135, 190, 245, 0.7)),
                    new Stop(0.6, Color.rgb(95, 150, 210, 0.7)),
                    new Stop(1.0, Color.rgb(65, 110, 160, 0.7))
            );
            gc.setFill(skyGradient);
        } catch (Exception e) {
            gc.setFill(Color.rgb(100, 160, 220, 0.7));
        }
        gc.fillRect(0, 0, 1000, 1000);
    }

    private void drawWind() {
        if (!simulation.isWeatherActive()) return;

        Image windImage = assets.windImage(simulation.getCurrentWindType());
        if (windImage == null || windImage.isError()) return;

        // windDirection: 0=up, 1=down, 2=right(base), 3=left
        double windRotation = switch (simulation.getWindDirection()) {
            case 0 -> -90.0;
            case 1 ->  90.0;
            case 2 ->   0.0;
            case 3 -> 180.0;
            default ->  0.0;
        };

        gc.save();
        gc.setGlobalAlpha(0.6);
        gc.translate(500, 500);
        gc.rotate(windRotation);
        gc.drawImage(windImage, -500, -500, 1000, 1000);
        gc.restore();
    }

    private void drawAirports() {
        Image redAirport = assets.getRedAirport();
        if (redAirport != null && !redAirport.isError()) {
            gc.drawImage(redAirport, (50 * SCALE_X) - 50, (500 * SCALE_Y) - 50, 140, 150);
        } else {
            drawBaseFallback(50 * SCALE_X, 500 * SCALE_Y, Color.INDIANRED, "RED AIRPORT");
        }

        Image blueAirport = assets.getBlueAirport();
        if (blueAirport != null && !blueAirport.isError()) {
            gc.drawImage(blueAirport, (950 * SCALE_X) - 90, (500 * SCALE_Y) - 50, 140, 150);
        } else {
            drawBaseFallback(950 * SCALE_X, 500 * SCALE_Y, Color.CORNFLOWERBLUE, "BLUE AIRPORT");
        }
    }

    private void drawProjectiles() {
        for (Projectile proj : simulation.getBoard().getProjectiles()) {
            double px = proj.x * SCALE_X;
            double py = proj.y * SCALE_Y;

            boolean isRedShooter = proj.getShooter().isRedTeam();
            Color coreColor = isRedShooter ? Color.ORANGE : Color.CYAN;
            Color glowColor = isRedShooter ? Color.rgb(255, 100, 0, 0.3) : Color.rgb(0, 200, 255, 0.3);

            double dx = proj.getVx();
            double dy = proj.getVy();

            gc.setStroke(glowColor);
            gc.setLineWidth(6);
            gc.strokeLine(px - dx, py - dy, px, py);

            gc.setStroke(coreColor);
            gc.setLineWidth(2.5);
            gc.strokeLine(px - dx * 0.7, py - dy * 0.7, px, py);
        }
    }

    private void drawBaseFallback(double x, double y, Color color, String name) {
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeRect(x - 20, y - 20, 40, 40);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(10));
        gc.fillText(name, x - 18, y - 25);
    }
}
