package oop.trening;

public class HotelReservation extends Reservation {
    private int nights;

    public HotelReservation(String code, String customerName, double price, int nights) {
        super(code, customerName, price);
        this.nights=nights;
    }

    public int getNights() {
        return nights;
    }
}
