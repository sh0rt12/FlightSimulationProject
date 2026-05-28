import java.util.ArrayList;
import java.util.List;

public class Board {
    private int width;
    private int height;

    private List<Plane> planes;
    private List<Projectile> projectiles;
    private List<Airport> airports;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;

        this.planes = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.airports = new ArrayList<>();
    }

    public void addPlane(Plane p) {
        planes.add(p);
    }

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public Plane getClosestEnemy(Plane currentPlane) {
        Plane closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Plane p : planes) {
            if (p.getClass() != currentPlane.getClass()) {
                double dist = Math.sqrt(Math.pow(currentPlane.x - p.x, 2) + Math.pow(currentPlane.y - p.y, 2));
                if (dist < minDistance) {
                    minDistance = dist;
                    closest = p;
                }
            }
        }
        return closest;
    }

    public void checkCollisions() {
        // Tu potem dodamy logikę trafień pocisków w samoloty
    }

    public List<Plane> getPlanes() {
        return planes;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public List<Airport> getAirports() {
        return airports;
    }

}

