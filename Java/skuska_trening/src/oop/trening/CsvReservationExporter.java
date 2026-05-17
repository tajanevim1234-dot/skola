package oop.trening;

import java.util.Collection;

public class CsvReservationExporter implements ReservationExporter {
    @Override
    public String export(Reservation reservation) {
        return 
                reservation.getCode()+"; "+
                reservation.getCustomerName()+"; "+
                reservation.getPrice()+"\n";
    }

    @Override
    public String export(Collection<Reservation> reservations) {
        String spac="code; customer; price\n";
        for(Reservation r : reservations){
            spac+=export(r);
        }
        return spac;
    }
}
