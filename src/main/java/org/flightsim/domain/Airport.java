package org.flightsim.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Lotnisko przyjmuje samoloty wracające z misji, serwisuje je (HP/amunicja/paliwo)
// i wypuszcza z powrotem. Mapa przechowuje czas spędzony w hangarze.
public class Airport {
    private final float x;
    private final float y;
    private final int capacity;        // max samolotów jednocześnie
    private final int maintenanceTime; // kroków do pełnego serwisu

    private final Map<Plane, Integer> parkedPlanes = new HashMap<>();

    public Airport(float x, float y, SimulationConfig config) {
        this.x               = x;
        this.y               = y;
        this.capacity        = config.getAirportCapacity();
        this.maintenanceTime = config.getMaintenanceTime();
    }

    // Lotnisko lewe (x<500) należy do czerwonych
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
        // zwiększamy licznik dla wszystkich zaparkowanych
        parkedPlanes.replaceAll((plane, timeSpent) -> timeSpent + 1);

        List<Plane> readyToLaunch = new ArrayList<>();
        for (Map.Entry<Plane, Integer> entry : parkedPlanes.entrySet()) {
            if (entry.getValue() >= maintenanceTime) {
                readyToLaunch.add(entry.getKey());
            }
        }

        readyToLaunch.forEach(this::launchPlane);
    }

    // Pełny restart samolotu — HP, amunicja, paliwo wracają do max
    private void launchPlane(Plane p) {
        parkedPlanes.remove(p);

        p.status.fuel         = p.getMaxFuel();
        p.status.ammo         = p.getMaxAmmo();
        p.status.hp           = p.getMaxHp();
        p.state               = PlaneState.FLYING;
        p.setCurrentSpeed(p.getBaseSpeed());
        // lekkie odsunięcie od lotniska żeby nie kręcił się w miejscu
        p.x += p.isRedTeam() ? 20.0f : -20.0f;
    }
}
