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
import java.util.Iterator;
import java.util.Random;

public class SimulationPanel extends Pane {

    private final Simulation simulation;
    private final Canvas canvas;
    private final GraphicsContext gc;

    private static final double SCALE_X = 1000.0 / 1000.0;
    private static final double SCALE_Y = 800.0 / 1000.0;

    private Image redAirport, blueAirport;
    private Image redJetFlying, redJetParked;
    private Image blueJetFlying, blueJetParked;

    private Image wind1Image, wind2Image, wind3Image;

    private double weatherOffsetY = 0;

    private final List<Explosion> explosions = new ArrayList<>();
    private final Random random = new Random();

    public SimulationPanel(Simulation simulation) {
        this.simulation = simulation;
        this.canvas = new Canvas(1000, 800);
        this.gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        loadImages();
    }

    private void loadImages() {
        try {
            redAirport = new Image(getClass().getResourceAsStream("/RED AIRPORT.png"));
            blueAirport = new Image(getClass().getResourceAsStream("/BLUE AIRPORT.png"));

            redJetFlying = new Image(getClass().getResourceAsStream("/FIGHTER JET RED FLYING.png"));
            redJetParked = new Image(getClass().getResourceAsStream("/FIGHTER JET RED PARKED.png"));
            blueJetFlying = new Image(getClass().getResourceAsStream("/FIGHTER JET BLUE FLYING.png"));
            blueJetParked = new Image(getClass().getResourceAsStream("/FIGHTER JET BLUE PARKED.png"));

            wind1Image = new Image(getClass().getResourceAsStream("/wiatr1.png"));
            wind2Image = new Image(getClass().getResourceAsStream("/wiatr2.png"));
            wind3Image = new Image(getClass().getResourceAsStream("/wiatr3.png"));

        } catch (Exception e) {
        }
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
        gc.clearRect(0, 0, 1000, 800);

        try {
            RadialGradient skyGradient = new RadialGradient(
                    0, 0, 500, 400, 600, false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.rgb(135, 190, 245, 0.7)),
                    new Stop(0.6, Color.rgb(95, 150, 210, 0.7)),
                    new Stop(1.0, Color.rgb(65, 110, 160, 0.7))
            );
            gc.setFill(skyGradient);
        } catch (Exception e) {
            gc.setFill(Color.rgb(100, 160, 220, 0.7));
        }
        gc.fillRect(0, 0, 1000, 800);

        if (simulation.isWeatherActive()) {
            Image currentWindImage = null;
            int type = simulation.getCurrentWindType();
            int dir = simulation.getWindDirection();

            if (type == 1) {
                currentWindImage = wind1Image;
            } else if (type == 2) {
                currentWindImage = wind2Image;
            } else if (type == 3) {
                currentWindImage = wind3Image;
            }

            if (currentWindImage != null && !currentWindImage.isError()) {
                weatherOffsetY += 15;
                if (weatherOffsetY > 800) {
                    weatherOffsetY = 0;
                }

                gc.save();
                gc.setGlobalAlpha(0.6);

                gc.translate(500, 400);
                gc.rotate(dir * 90);

                gc.drawImage(currentWindImage, -500, -400 + weatherOffsetY, 1000, 800);
                gc.drawImage(currentWindImage, -500, -400 + weatherOffsetY - 800, 1000, 800);

                gc.restore();
                gc.setGlobalAlpha(1.0);
            } else {
                gc.setStroke(Color.rgb(150, 150, 255, 0.5));
                gc.setLineWidth(2);
                for(int i=0; i<100; i++) {
                    double rx = random.nextDouble() * 1000;
                    double ry = random.nextDouble() * 800;
                    gc.strokeLine(rx, ry, rx - 10, ry + 20);
                }
            }
        }

        if (redAirport != null && !redAirport.isError()) {
            gc.drawImage(redAirport, (50 * SCALE_X) - 50, (500 * SCALE_Y) - 50, 140, 150);
        } else {
            drawBaseFallback(50 * SCALE_X, 500 * SCALE_Y, Color.INDIANRED, "RED AIRPORT");
        }

        if (blueAirport != null && !blueAirport.isError()) {
            gc.drawImage(blueAirport, (950 * SCALE_X) - 90, (500 * SCALE_Y) - 50, 140, 150);
        } else {
            drawBaseFallback(950 * SCALE_X, 500 * SCALE_Y, Color.CORNFLOWERBLUE, "BLUE AIRPORT");
        }

        List<Projectile> projectiles = simulation.getBoard().getProjectiles();
        for (Projectile proj : projectiles) {
            double px = proj.x * SCALE_X;
            double py = proj.y * SCALE_Y;

            boolean isRedShooter = (proj.getShooter() instanceof RedPlane);
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

        List<Plane> planes = simulation.getBoard().getPlanes();
        for (Plane p : planes) {
            drawPlane(p);
        }

        Iterator<Explosion> it = explosions.iterator();
        while (it.hasNext()) {
            Explosion exp = it.next();
            double progress = (double) exp.currentFrame / exp.maxFrames;
            double maxRadius = 45.0;
            double currentRadius = maxRadius * Math.sin(progress * Math.PI / 2);

            gc.setFill(Color.rgb(255, 60, 0, 1.0 - progress));
            gc.fillOval(exp.x - currentRadius, exp.y - currentRadius, currentRadius * 2, currentRadius * 2);

            gc.setFill(Color.rgb(255, 200, 0, 1.0 - progress));
            gc.fillOval(exp.x - (currentRadius * 0.6), exp.y - (currentRadius * 0.6), currentRadius * 1.2, currentRadius * 1.2);

            if (exp.update()) {
                it.remove();
            }
        }

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Segoe UI", 14));
        gc.fillText("Step: " + simulation.getStepCount(), 15, 25);
    }

    private void drawPlane(Plane p) {
        double px = p.x * SCALE_X;
        double py = p.y * SCALE_Y;

        Image jetImage = null;
        boolean isRed = (p instanceof RedPlane);

        if (p.state == PlaneState.PARKED) {
            jetImage = isRed ? redJetParked : blueJetParked;
        } else {
            jetImage = isRed ? redJetFlying : blueJetFlying;
        }

        double angle = 0;

        if (p.state == PlaneState.PARKED) {
            angle = isRed ? 90 : -90;
        } else {
            double targetX = p.x;
            double targetY = p.y;

            if (p.state == PlaneState.RETURNING_TO_BASE) {
                targetX = p.baseX;
                targetY = p.baseY;
            } else if (p.target != null) {
                targetX = p.target.x;
                targetY = p.target.y;
            } else {
                targetX = isRed ? 1000 : 0;
                targetY = p.y;
            }

            double dx = targetX - p.x;
            double dy = targetY - p.y;

            if (Math.abs(dx) > 0.1 || Math.abs(dy) > 0.1) {
                angle = Math.toDegrees(Math.atan2(dy, dx)) + 90;
            } else {
                angle = isRed ? 90 : -90;
            }
        }

        if (p.state == PlaneState.PARKED) {
            px += (p.id % 4 - 1.5) * 16;
            py += (p.id / 4 % 4 - 1.5) * 16;
        }

        double size = 32.0;

        gc.save();
        gc.translate(px, py);
        gc.rotate(angle);

        if (p.state != PlaneState.PARKED) {
            double flameLength = 10.0 + random.nextDouble() * 8.0;
            double flameWidth = 6.0;

            gc.setFill(Color.ORANGE);
            gc.fillPolygon(
                    new double[]{-flameWidth/2, 0, flameWidth/2},
                    new double[]{size/2, size/2 + flameLength, size/2},
                    3
            );

            gc.setFill(Color.YELLOW);
            gc.fillPolygon(
                    new double[]{-flameWidth/3, 0, flameWidth/3},
                    new double[]{size/2, size/2 + (flameLength * 0.6), size/2},
                    3
            );
        }

        if (jetImage == null || jetImage.isError()) {
            gc.setFill(isRed ? Color.RED : Color.BLUE);
            gc.fillOval(-6, -6, 12, 12);
        } else {
            gc.drawImage(jetImage, -size / 2, -size / 2, size, size);
        }

        gc.restore();
    }

    private void drawBaseFallback(double x, double y, Color color, String name) {
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeRect(x - 20, y - 20, 40, 40);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(10));
        gc.fillText(name, x - 18, y - 25);
    }

    private static class Explosion {
        double x, y;
        int currentFrame = 0;
        final int maxFrames = 8;

        Explosion(double x, double y) {
            this.x = x;
            this.y = y;
        }

        boolean update() {
            currentFrame++;
            return currentFrame >= maxFrames;
        }
    }
}