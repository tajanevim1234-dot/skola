package oop.zadanie7;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class Warehouse {

    private Map<String, Product> products;

    public Warehouse() {
        this.products = new HashMap<>();
    }

    public void addProduct(Product p) throws ProductAlreadyExistsException {
        if (products.containsKey(p.getId())) {
            throw new ProductAlreadyExistsException("Product s ID '" + p.getId() + "' sa v sklade už nachádza.");
        }

        products.put(p.getId(), p);
    }

    public void removeProduct(String id) throws ProductNotFoundException {
        if (!products.containsKey(id)) {
            throw new ProductNotFoundException("Product s ID '" + id + "' sa v sklade nenachádza.");
        }

        products.remove(id);
    }

    public Product getProduct(String id) throws ProductNotFoundException {
        if (!products.containsKey(id)) {
            throw new ProductNotFoundException("Product s ID '" + id + "' sa v sklade nenachádza.");
        }

        return products.get(id);
    }

    public int getProductCount() {
        return products.size();
    }

    public double calculateTotalValue() {
        double sum = 0;

        for (Product p : products.values()) {
            sum += p.getPrice();
        }

        return sum;
    }

    public List<Product> findProductsByPriceRange(double min, double max) {
        List<Product> result = new ArrayList<>();

        for (Product p : products.values()) {
            if (p.getPrice() >= min && p.getPrice() <= max) {
                result.add(p);
            }
        }

        return result;
    }

    public List<Product> getProductsSortedByPrice() {
        List<Product> result = new ArrayList<>(products.values());

        Collections.sort(result, new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                return Double.compare(p1.getPrice(), p2.getPrice());
            }
        });

        return result;
    }

    public List<Product> getProductsSortedById() {
        List<Product> result = new ArrayList<>(products.values());

        Collections.sort(result);

        return result;
    }

    public <T extends Product> List<T> getProductsByType(Class<T> type) {
        List<T> result = new ArrayList<>();

        for (Product p : products.values()) {
            if (type.isInstance(p)) {
                result.add(type.cast(p));
            }
        }

        return result;
    }

    public Product findProductWithSmallestId() {
        if (products.isEmpty()) {
            return null;
        }

        Product smallest = null;

        for (Product p : products.values()) {
            if (smallest == null || p.getId().compareTo(smallest.getId()) < 0) {
                smallest = p;
            }
        }

        return smallest;
    }

}