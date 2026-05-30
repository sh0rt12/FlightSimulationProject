import java.util.Map;
import java.util.HashMap;

public class Airport {
    private int capacity;
    private Map<Plane, Integer> parkedPlanes = new HashMap<>();

    public void dockPlane(Plane p) {
        p.fuel = p.maxFuel;
        p.ammo = p.maxAmmo;
        p.state = PlaneState.FLYING;
        parkedPlanes.put(p, 0);
    }

    public void processTurn() {

    }

    public void launchPlane(Plane p) {
        parkedPlanes.remove(p);
    }
}