package oop.zadanie6;

import java.util.HashSet;
import java.util.Set;

public class Warehouse {
    private Set<Product> products;

    public Warehouse() {
        products = new HashSet<>();
    }

    public void addProduct(Product p) throws ProductAlreadyExistsException {
        if (products.contains(p)) {
            throw new ProductAlreadyExistsException(p.getId());
        }
        products.add(p);
    }

    public void removeProduct(String id) throws ProductNotFoundException {

        for(Product p : products){
            if(p.getId().equals(id)){
                products.remove(p);
                return;
            }
        }
    
        throw new ProductNotFoundException("Product s ID '" + id + "' sa v sklade nenachádza.");
    }

    public int getProductCount() {
        return products.size();
    }
}