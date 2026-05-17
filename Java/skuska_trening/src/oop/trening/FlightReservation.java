package oop.trening;

public class FlightReservation extends Reservation implements Upgradable {
    private String seatClass;

    public FlightReservation(String code, String customerName, double price, String seatClass) {
        super(code, customerName, price);
        this.seatClass=seatClass;
    }

    public String getSeatClass() {
        return seatClass;
    }

    @Override
    public void upgrade() {
        if(this.seatClass.equals("economy")){
            this.seatClass="business";
            this.setPrice(this.getPrice()+150);
        }else if (this.seatClass.equals("business")){
            this.seatClass="first";
            this.setPrice(this.getPrice()+300);
        } 
    }
}
