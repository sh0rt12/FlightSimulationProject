package org.flightsim.domain;

// Mechanika ruchu — wyznacza wektor na podstawie stanu AI i przesuwa samolot.
// Separacja zapobiega nakładaniu się samolotów (boids-style, uproszczona wersja).
public class PlaneMovement {

    private static final float SEP_RADIUS = 24.0f; // strefa w której samoloty się odpychają

    private final Plane plane;

    public PlaneMovement(Plane plane) {
        this.plane = plane;
    }

    public void move(Board board) {
        if (plane.state == PlaneState.DEAD || plane.state == PlaneState.PARKED) return;

        if (plane.state == PlaneState.RETURNING_TO_BASE && handleArrivalAtBase(board)) return;

        float[] dir = steeringDirection(board);
        float moveX = dir[0];
        float moveY = dir[1];

        // mieszamy kierunek docelowy (60%) z siłą separacji (40%)
        float[] sep    = separationForce(board);
        float   sepLen = (float) Math.sqrt(sep[0] * sep[0] + sep[1] * sep[1]);
        if (sepLen > 0) {
            moveX = moveX * 0.6f + (sep[0] / sepLen) * 0.4f;
            moveY = moveY * 0.6f + (sep[1] / sepLen) * 0.4f;
            float len = (float) Math.sqrt(moveX * moveX + moveY * moveY);
            if (len > 0) { moveX /= len; moveY /= len; }
        }

        plane.lastVx = moveX;
        plane.lastVy = moveY;

        commitPosition(board, moveX, moveY);
    }

    // true = samolot zadokował lub uciekł poza mapę, kończymy turę wcześniej
    private boolean handleArrivalAtBase(Board board) {
        float  dx       = plane.baseX - plane.x;
        float  dy       = plane.baseY - plane.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance >= 15.0) return false;

        Airport airport = board.getAirportFor(plane);
        if (airport != null && airport.canDock()) {
            airport.dockPlane(plane);
        } else {
            // hangar pełny — samolot ucieka poza mapę i ginie
            tryEscapeOffMap();
        }
        return true;
    }

    private float[] steeringDirection(Board board) {
        return switch (plane.state) {
            case RETURNING_TO_BASE -> directionTo(plane.baseX, plane.baseY);
            case FIGHTING          -> hasLiveTarget() ? orbitMove() : new float[]{0, 0};
            case EVADING           -> evadeDirection();
            default                -> chaseDirection();
        };
    }

    private float[] chaseDirection() {
        if (hasLiveTarget()) {
            return directionTo(plane.target.x, plane.target.y);
        }
        // brak celu — leć w stronę przeciwnika (prawa/lewa strona mapy)
        return new float[]{ plane.isRedTeam() ? 1.0f : -1.0f, 0.0f };
    }

    // Manewr unikowy: ruch prostopadły do linii do wroga z sinusoidalną siłą
    private float[] evadeDirection() {
        if (!hasLiveTarget()) {
            plane.state = PlaneState.RETURNING_TO_BASE;
            return new float[]{0, 0};
        }

        float  dx       = plane.target.x - plane.x;
        float  dy       = plane.target.y - plane.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= 0) return new float[]{0, 0};

        float normX = (float) (dx / distance);
        float normY = (float) (dy / distance);

        float progress        = (float) plane.status.evadeTimer / plane.stats.evadeDuration;
        float evasionStrength  = (float) Math.sin(progress * Math.PI); // sinusoida — max w środku manewru
        float moveX = -normY * plane.status.evadeDirection * evasionStrength
                + normX * (1.0f - evasionStrength * 0.5f);
        float moveY =  normX * plane.status.evadeDirection * evasionStrength
                + normY * (1.0f - evasionStrength * 0.5f);

        double len = Math.sqrt(moveX * moveX + moveY * moveY);
        if (len > 0) { moveX /= len; moveY /= len; }
        return new float[]{ moveX, moveY };
    }

    // Orbita wokół celu — kąt przyrastający o 0.08 rad/krok
    private float[] orbitMove() {
        double orbitRadius  = plane.stats.fightRange * 0.8;
        double anglePerStep = 0.08;

        plane.status.orbitAngle += anglePerStep;

        double targetX = plane.target.x + Math.cos(plane.status.orbitAngle) * orbitRadius;
        double targetY = plane.target.y + Math.sin(plane.status.orbitAngle) * orbitRadius;

        return directionTo((float) targetX, (float) targetY);
    }

    // Odpychanie od sąsiadów — siła rośnie kwadratowo im bliżej
    private float[] separationForce(Board board) {
        float sepX = 0, sepY = 0;
        for (Plane other : board.getPlanes()) {
            if (other == plane || other.state == PlaneState.DEAD || other.state == PlaneState.PARKED) continue;
            float dx   = plane.x - other.x;
            float dy   = plane.y - other.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist > 0 && dist < SEP_RADIUS) {
                float strength = (SEP_RADIUS - dist) / SEP_RADIUS;
                strength *= strength;
                sepX += (dx / dist) * strength;
                sepY += (dy / dist) * strength;
            }
        }
        return new float[]{ sepX, sepY };
    }

    private void commitPosition(Board board, float moveX, float moveY) {
        double windMod = (board.getSimulation() != null)
                ? board.getSimulation().getWindMultiplier(plane)
                : 1.0;

        // walka — wolniej, unik — szybciej
        float speedMod = switch (plane.state) {
            case FIGHTING -> 0.75f;
            case EVADING  -> 1.3f;
            default       -> 1.0f;
        };

        float nextX = plane.x + (float) (moveX * plane.status.currentSpeed * speedMod * windMod);
        float nextY = plane.y + (float) (moveY * plane.status.currentSpeed * speedMod * windMod);

        // odbij od krawędzi planszy
        if (nextX < 30.0f)  nextX = 30.0f;
        if (nextX > 970.0f) nextX = 970.0f;
        if (nextY < 30.0f)  nextY = 30.0f;
        if (nextY > 970.0f) nextY = 970.0f;

        plane.x = nextX;
        plane.y = nextY;
    }

    // Gdy hangar pełny — samolot ucieka w kierunku przeciwnym do bazy i znika z mapy
    private void tryEscapeOffMap() {
        float  dx       = plane.baseX - plane.x;
        float  dy       = plane.baseY - plane.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            float escapeX = (float) (-dx / distance);
            float escapeY = (float) (-dy / distance);
            plane.lastVx = escapeX;
            plane.lastVy = escapeY;
            plane.x += escapeX * plane.status.currentSpeed;
            plane.y += escapeY * plane.status.currentSpeed;
        }

        if (plane.x < 0 || plane.x > 1000 || plane.y < 0 || plane.y > 1000) {
            plane.state = PlaneState.DEAD;
        }
    }

    private boolean hasLiveTarget() {
        return plane.target != null && plane.target.state != PlaneState.DEAD;
    }

    private float[] directionTo(float targetX, float targetY) {
        float  dx   = targetX - plane.x;
        float  dy   = targetY - plane.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            return new float[]{ (float) (dx / dist), (float) (dy / dist) };
        }
        return new float[]{ 0, 0 };
    }
}
