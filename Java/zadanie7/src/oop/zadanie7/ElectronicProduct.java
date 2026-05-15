package oop.zadanie7;

public class ElectronicProduct extends Product implements Discountable {
    private int warrantyMonths;

    public ElectronicProduct(String id, String name, double price, int warrantyMonths) {
        super(id, name, price);
        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public void applyDiscount(double percentage) {
        if (percentage > 0 && percentage < 100) {
            double discount = getPrice() * (percentage / 100);
            setPrice(getPrice() - discount);
        }
    }

    @Override
    public String toString() {
        return "ElectronicProduct{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", price=" + getPrice() +
                ", warrantyMonths=" + warrantyMonths +
                '}';
    }
}
