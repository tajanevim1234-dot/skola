package oop.trening;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationSystem {
    private Map<String, Reservation> reservations = new HashMap<>();
    private ReservationExporter exporter;

    public void addReservation(Reservation reservation) throws DuplicateReservationException {
        // TODO
    }

    public void removeReservation(String code) throws ReservationNotFoundException {
        // TODO
    }

    public Reservation getReservation(String code) {
        return null;
    }

    public int getReservationCount() {
        return 0;
    }

    public List<Reservation> getReservationsSortedByCode() {
        return List.of();
    }

    public List<Reservation> findReservationsByPriceRange(double min, double max) {
        return List.of();
    }

    public double calculateTotalPrice() {
        return 0;
    }

    public <T extends Reservation> List<T> getReservationsByType(Class<T> type) {
        return List.of();
    }

    public void setExporter(ReservationExporter exporter) {
        this.exporter = exporter;
    }

    public String exportReservations() {
        Collection<Reservation> values = reservations.values();
        return exporter == null ? "" : exporter.export(values);
    }
}
