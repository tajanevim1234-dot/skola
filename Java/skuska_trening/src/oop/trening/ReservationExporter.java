package oop.trening;

import java.util.Collection;

public interface ReservationExporter {
    String export(Reservation reservation);
    String export(Collection<Reservation> reservations);
}
