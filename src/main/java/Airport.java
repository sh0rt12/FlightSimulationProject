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

    public String getColor() {
        if (this.x < 500) {
            return "RED";
        } else {
            return "BLUE";
        }
    }

    public boolean canDock() {
        return parkedPlanes.size() < capacity;
    }

    public void dockPlane(Plane p) {
        if (canDock()) {
            p.state = PlaneState.PARKED;
            p.x = this.x;
            p.y = this.y;
            p.currentSpeed = 0;

            parkedPlanes.put(p, 0);
        }
    }

    public void processTurn() {
        List<Plane> readyToLaunch = new ArrayList<>();

        parkedPlanes.replaceAll((plane, timeSpent) -> timeSpent + 1);

        for (Map.Entry<Plane, Integer> entry : parkedPlanes.entrySet()) {
            if (entry.getValue() >= maintenanceTime) {
                readyToLaunch.add(entry.getKey());
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
        p.currentSpeed = p.baseSpeed;

        boolean isRed = (p instanceof RedPlane);
        p.x += isRed ? 20.0f : -20.0f;
    }
}