import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class Airport {
    private final float x;
    private final float y;
    private final int capacity = 5;
    private final int maintenanceTime = 40;

    private final Map<Plane, Integer> parkedPlanes = new HashMap<>();

    public Airport(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean canDock() {
        return parkedPlanes.size() < capacity;
    }

    public void dockPlane(Plane p) {
        if (canDock()) {
            p.state = PlaneState.PARKED;
            p.x = this.x;
            p.y = this.y;
            parkedPlanes.put(p, 0);
        }
    }

    public void processTurn() {
        List<Plane> readyToLaunch = new ArrayList<>();

        for (Map.Entry<Plane, Integer> entry : parkedPlanes.entrySet()) {
            Plane plane = entry.getKey();
            int timeSpent = entry.getValue() + 1;

            parkedPlanes.put(plane, timeSpent);

            if (timeSpent >= maintenanceTime) {
                readyToLaunch.add(plane);
            }
        }

        for (Plane plane : readyToLaunch) {
            launchPlane(plane);
        }
    }

    public void launchPlane(Plane p) {
        parkedPlanes.remove(p);
        p.fuel = p.maxFuel;
        p.ammo = p.maxAmmo;
        p.hp = 3;
        p.state = PlaneState.FLYING;
    }

    public float getX() { return x; }
    public float getY() { return y; }
}