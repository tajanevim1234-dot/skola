package oop.zadanie6;

public abstract class Product {
    private String id;
    private String name;
    private double price;

    public Product(String id,String name,double price){
        this.id=id;
        this.name=name;
        this.price=price;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;              
        if (o == null || getClass() != o.getClass()) return false; 

        Product product = (Product) o;           
        return id.equals(product.id);           
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "', price=" + price + "}";
    }
}