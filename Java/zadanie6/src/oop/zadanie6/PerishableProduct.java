package oop.zadanie6;

public class PerishableProduct extends Product {

    private int expirationDays;

    public PerishableProduct(String id,String name,double price,int expirationDays){
        super(id,name,price);
        this.expirationDays=expirationDays;
    }

    public void setExpirationDays(int expirationDays) {
        this.expirationDays = expirationDays;
    }
    
    public int getExpirationDays() {
        return expirationDays;
    }

    @Override
    public String toString() {
        return "Product{id='" + this.getId() + "', name='" + this.getName() + "', price=" + this.getPrice() + ", expirationDays=" + this.expirationDays + "}";
    }
}
