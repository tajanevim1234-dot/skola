package oop.trening;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationSystem {
    private Map<String, Reservation> reservations = new HashMap<>();
    private ReservationExporter exporter;

    public void addReservation(Reservation reservation) throws DuplicateReservationException {
        if(reservations.containsKey(reservation.getCode())){
            throw new DuplicateReservationException("toto tam uz je");
        }
        reservations.put(reservation.getCode(), reservation);
    }

    public void removeReservation(String code) throws ReservationNotFoundException {
        if(reservations.containsKey(code)){
            reservations.remove(code);
            return;
        }
        throw new ReservationNotFoundException("nenasiel som");
    }

    public Reservation getReservation(String code) {
        return reservations.get(code);
    }

    public int getReservationCount() {
        return reservations.size();
    }

    public List<Reservation> getReservationsSortedByCode() {
        List<Reservation> moj = new ArrayList<>(reservations.values());
        Collections.sort(moj);
        return moj;
    }

    public List<Reservation> findReservationsByPriceRange(double min, double max) {
        List<Reservation> moj = new ArrayList<>();
        for(Reservation r : reservations.values()){
            if(r.getPrice() >= min && r.getPrice() <= max){
                moj.add(r);
            }
        }
        return moj;
    }

    public double calculateTotalPrice() {
        double price=0;
        for(Reservation r : reservations.values()){
            price+=r.getPrice();
        }
        return price;
    }

    public <T extends Reservation> List<T> getReservationsByType(Class<T> type) {
        List<T> moj = new ArrayList<>();

        for (Reservation r : reservations.values()) {
            if (type.isInstance(r)) {
                moj.add(type.cast(r));
            }
        }

        return moj;
    }

    public void setExporter(ReservationExporter exporter) {
        this.exporter = exporter;
    }

    public String exportReservations() {
        Collection<Reservation> values = reservations.values();
        return exporter == null ? "" : exporter.export(values);
    }
}
