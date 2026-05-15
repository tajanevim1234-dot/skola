package test;

public class CarReservation extends Reservation {

    private String licencePlate;

    public CarReservation(String resevationId, String customerName,double pricePerNight,int nights,String licencePlate){
        super(resevationId, customerName, pricePerNight, nights);
        this.licencePlate=licencePlate;
    }
    
    public void setlicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getlicencePlate() {
        return licencePlate;
    }

    @Override
    public double getTotalPrice(){
        return this.getPricePerNight() * this.getNights();
    }


}
