import java.util.ArrayList;
import java.util.List;

public class Board {
    private double width;
    private double height;
    private List<Plane> planes;
    private List<Projectile> projectiles;
    private List<Airport> airports;
    private Simulation simulation;

    public Board(double width, double height) {
        this.width = width;
        this.height = height;
        this.planes = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.airports = new ArrayList<>();
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Simulation getSimulation() {
        return this.simulation;
    }

    public List<Plane> getPlanes() {
        return this.planes;
    }

    public List<Projectile> getProjectiles() {
        return this.projectiles;
    }

    public List<Airport> getAirports() {
        return this.airports;
    }

    public void addPlane(Plane p) {
        this.planes.add(p);
    }

    public void addProjectile(Projectile p) {
        this.projectiles.add(p);
    }

    public Plane getClosestEnemy(Plane activePlane) {
        Plane closest = null;
        double minDistance = Double.MAX_VALUE;
        boolean isActiveRed = (activePlane instanceof RedPlane);

        for (Plane p : planes) {
            if (p.state == PlaneState.DEAD || p.state == PlaneState.PARKED) continue;
            boolean isTargetRed = (p instanceof RedPlane);
            if (isActiveRed != isTargetRed) {
                double dx = p.x - activePlane.x;
                double dy = p.y - activePlane.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < minDistance) {
                    minDistance = dist;
                    closest = p;
                }
            }
        }
        return closest;
    }

    public Airport getAirportFor(Plane p) {
        boolean isRed = (p instanceof RedPlane);
        for (Airport a : airports) {
            if (isRed && a.getColor().equals("RED")) return a;
            if (!isRed && a.getColor().equals("BLUE")) return a;
        }
        if (!airports.isEmpty()) {
            return airports.get(0);
        }
        return null;
    }

    public void checkCollisions() {
        for (Projectile proj : new ArrayList<>(projectiles)) {
            for (Plane plane : new ArrayList<>(planes)) {
                if (plane.state == PlaneState.DEAD || plane.state == PlaneState.PARKED) continue;
                if (proj.getShooter() instanceof RedPlane && plane instanceof RedPlane) continue;
                if (proj.getShooter() instanceof BluePlane && plane instanceof BluePlane) continue;

                double dx = proj.x - plane.x;
                double dy = proj.y - plane.y;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < 20.0) {
                    plane.takeDamage(1);
                    projectiles.remove(proj);
                    break;
                }
            }
        }
    }
}