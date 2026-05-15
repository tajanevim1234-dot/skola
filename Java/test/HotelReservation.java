package test;

public class HotelReservation extends Reservation implements Upgradable {

    private int roomNumber;

    public HotelReservation(String resevationId, String customerName,double pricePerNight,int nights,int roomNumber){
        super(resevationId, customerName, pricePerNight, nights);
        this.roomNumber=roomNumber;
    }
    
    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    @Override
    public double getTotalPrice(){
        return this.getPricePerNight() * this.getNights();
    }

    @Override
    public void applyUpgrade(double extraCostPerNight) {
        if(extraCostPerNight>0){
            this.setPricePerNight(this.getPricePerNight()+extraCostPerNight);
        }
    }


}
