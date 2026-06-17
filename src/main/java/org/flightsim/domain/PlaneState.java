package org.flightsim.domain;

// Stany w jakich może być samolot podczas symulacji
public enum PlaneState {
    FLYING,             // normalny lot, szuka wroga
    FIGHTING,           // aktywna walka (orbit wokół celu)
    EVADING,            // manewr unikowy po trafieniu
    RETURNING_TO_BASE,  // leci do lotniska (brak paliwa/amunicji albo mało HP)
    PARKED,             // w hangarze, trwa serwis
    DEAD                // zestrzelony, do usunięcia z planszy
}
