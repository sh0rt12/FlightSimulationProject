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

        this.airports.add(new Airport(50.0f, 500.0f));
        this.airports.add(new Airport(950.0f, 500.0f));
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
            if (p.getClass() != currentPlane.getClass() && p.state != PlaneState.DEAD && p.state != PlaneState.PARKED) {
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
        for (Projectile proj : new ArrayList<>(projectiles)) {
            for (Plane plane : new ArrayList<>(planes)) {

                if (proj.getShooter().getClass() != plane.getClass() &&
                        plane.state != PlaneState.DEAD &&
                        plane.state != PlaneState.PARKED) {

                    float dx = proj.x - plane.x;
                    float dy = proj.y - plane.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    if (dist < 12.0) {
                        plane.takeDamage(1);
                        projectiles.remove(proj);
                        break;
                    }
                }
            }
        }
    }

    public Airport getAirportFor(Plane p) {
        if (p.baseX < 500) {
            return airports.get(0);
        } else {
            return airports.get(1);
        }
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

