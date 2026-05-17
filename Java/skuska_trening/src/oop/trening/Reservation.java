package oop.trening;

public abstract class Reservation implements Comparable<Reservation> {
    private String code;
    private String customerName;
    private double price;

    public Reservation(String code, String customerName, double price) throws IllegalArgumentException {
        if(price<=0){
            throw new IllegalArgumentException("zadaj dobru cenu");
        }
        this.code=code;
        this.customerName=customerName;
        this.price=price;
    }

    public String getCode() {
        return code;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public int compareTo(Reservation other) {
        if(this.code.equals(other.code)){
            return 1;
        }
        return 0;
    }

    @Override
    public int HashCode(Reservation other){
        return other.code;
    }
}
