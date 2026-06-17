package org.flightsim.domain;

import java.util.ArrayList;
import java.util.List;

// Plansza — trzyma listy samolotów, pocisków i lotnisk.
// checkCollisions() jest tutaj bo potrzebuje dostępu do wszystkich trzech.
public class Board {
    private final List<Plane>      planes      = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final List<Airport>    airports    = new ArrayList<>();
    private Simulation simulation; // referencja wsteczna do silnika (do statystyk i configu)

    public void setSimulation(Simulation simulation) { this.simulation = simulation; }
    public Simulation getSimulation()                { return this.simulation; }

    public List<Plane>      getPlanes()      { return planes; }
    public List<Projectile> getProjectiles() { return projectiles; }
    public List<Airport>    getAirports()    { return airports; }

    public void addPlane(Plane p)           { planes.add(p); }

    // Przy dodaniu pocisku od razu liczymy strzał do statystyk
    public void addProjectile(Projectile p) {
        projectiles.add(p);
        if (simulation != null) {
            if (p.getShooter().isRedTeam()) {
                simulation.incrementRedShots();
            } else {
                simulation.incrementBlueShots();
            }
        }
    }

    // Szuka najbliższego żywego wroga — AI używa tego co krok
    public Plane getClosestEnemy(Plane activePlane) {
        Plane  closest     = null;
        double minDistance = Double.MAX_VALUE;

        for (Plane p : planes) {
            if (p.state == PlaneState.DEAD || p.state == PlaneState.PARKED) continue;
            if (p.isRedTeam() == activePlane.isRedTeam()) continue;

            double dx   = p.x - activePlane.x;
            double dy   = p.y - activePlane.y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < minDistance) {
                minDistance = dist;
                closest     = p;
            }
        }
        return closest;
    }

    public Airport getAirportFor(Plane p) {
        for (Airport a : airports) {
            if (a.isRedAirport() == p.isRedTeam()) return a;
        }
        return airports.isEmpty() ? null : airports.get(0);
    }

    // Sprawdza trafienia — promień 20px to kompromis między precyzją a grywalnym tempem walki
    public void checkCollisions() {
        for (Projectile proj : new ArrayList<>(projectiles)) {
            for (Plane plane : new ArrayList<>(planes)) {
                if (plane.state == PlaneState.DEAD || plane.state == PlaneState.PARKED) continue;
                if (proj.getShooter().isRedTeam() == plane.isRedTeam()) continue;

                double dx   = proj.x - plane.x;
                double dy   = proj.y - plane.y;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < 20.0) {
                    plane.takeDamage(1);
                    projectiles.remove(proj);
                    if (simulation != null) {
                        if (proj.getShooter().isRedTeam()) {
                            simulation.incrementRedHits();
                        } else {
                            simulation.incrementBlueHits();
                        }
                    }
                    break;
                }
            }
        }
    }
}
