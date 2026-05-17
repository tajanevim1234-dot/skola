package oop.trening;

public class FlightReservation extends Reservation implements Upgradable {
    private String seatClass;

    public FlightReservation(String code, String customerName, double price, String seatClass) {
        super(code, customerName, price);
        // TODO
    }

    public String getSeatClass() {
        return seatClass;
    }

    @Override
    public void upgrade() {
        // TODO
    }
}
