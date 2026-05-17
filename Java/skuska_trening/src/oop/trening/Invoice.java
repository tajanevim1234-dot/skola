package oop.trening;

public class Invoice {
    private String customer;
    private String content;
    private double total;

    Invoice(String customer, String content, double total) {
        this.customer=customer;
        this.content=content;
        this.total=total;
    }

    public String getCustomer() {
        return customer;
    }

    public String getContent() {
        return content;
    }

    public double getTotal() {
        return total;
    }
}
