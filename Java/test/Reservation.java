package test;

public abstract class Reservation {

    private String resevationId;
    private String customerName;
    private double pricePerNight;
    private int nights;

    public Reservation(String resevationId, String customerName,double pricePerNight,int nights){
        this.resevationId=resevationId;
        this.customerName=customerName;
        this.pricePerNight=pricePerNight;
        this.nights=nights;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public int getNights() {
        return nights;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public String getResevationId() {
        return resevationId;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getCustomerName() {
        return customerName;
    }

    public abstract double getTotalPrice();

    @Override
    public boolean equals(Object s){
        if(!(s instanceof Reservation)) return false;
        Reservation a = (Reservation) s;
        return this.resevationId.equals(a.resevationId);
    }

    @Override
    public int hashCode(){
        return resevationId.hashCode();
    }

}