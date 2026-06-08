import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Airport {
    private final float x;
    private final float y;
    private final int capacity;
    private final int maintenanceTime;

    private final Map<Plane, Integer> parkedPlanes = new HashMap<>();

    public Airport(float x, float y, SimulationConfig config) {
        this.x               = x;
        this.y               = y;
        this.capacity        = config.getAirportCapacity();
        this.maintenanceTime = config.getMaintenanceTime();
    }

    public boolean isRedAirport() {
        return this.x < 500;
    }

    public boolean canDock() {
        return parkedPlanes.size() < capacity;
    }

    public void dockPlane(Plane p) {
        if (!canDock()) return;
        p.state           = PlaneState.PARKED;
        p.x               = this.x;
        p.y               = this.y;
        p.setCurrentSpeed(0);
        parkedPlanes.put(p, 0);
    }

    public void processTurn() {
        parkedPlanes.replaceAll((plane, timeSpent) -> timeSpent + 1);

        List<Plane> readyToLaunch = new ArrayList<>();
        for (Map.Entry<Plane, Integer> entry : parkedPlanes.entrySet()) {
            if (entry.getValue() >= maintenanceTime) {
                readyToLaunch.add(entry.getKey());
            }
        }

        readyToLaunch.forEach(this::launchPlane);
    }

    private void launchPlane(Plane p) {
        parkedPlanes.remove(p);

        p.status.fuel         = p.getMaxFuel();
        p.status.ammo         = p.getMaxAmmo();
        p.status.hp           = p.getMaxHp();
        p.state               = PlaneState.FLYING;
        p.setCurrentSpeed(p.getBaseSpeed());
        p.x += p.isRedTeam() ? 20.0f : -20.0f;
    }
}
