package oop.zadanie7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WarehouseLoader {

    public List<Product> loadProducts(String filename) throws InvalidProductFormatException {
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 5) {
                    throw new InvalidProductFormatException("Invalid product format: " + line);
                }

                String type = parts[0];
                String id = parts[1];
                String name = parts[2];
                double price = Double.parseDouble(parts[3]);
                String extra = parts[4];

                Product product;

                if (type.equals("E")) {
                    int warrantyMonths = Integer.parseInt(extra);
                    product = new ElectronicProduct(id, name, price, warrantyMonths);
                } else if (type.equals("P")) {
                    LocalDate expirationDate = LocalDate.parse(extra);
                    product = new PerishableProduct(id, name, price, expirationDate);
                } else {
                    throw new InvalidProductFormatException("Unknown product type: " + type);
                }

                products.add(product);
            }
        } catch (IOException e) {
            throw new InvalidProductFormatException("Cannot read file: " + filename);
        } catch (NumberFormatException e) {
            throw new InvalidProductFormatException("Invalid number format");
        }

        Collections.sort(products);
        return products;
    }

    public void saveProducts(List<Product> products, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            for (Product p : products) {

                if (p instanceof ElectronicProduct) {
                    ElectronicProduct e = (ElectronicProduct) p;

                    writer.println("E," +
                            e.getId() + "," +
                            e.getName() + "," +
                            e.getPrice() + "," +
                            e.getWarrantyMonths());

                } else if (p instanceof PerishableProduct) {
                    PerishableProduct perishable = (PerishableProduct) p;

                    writer.println("P," +
                            perishable.getId() + "," +
                            perishable.getName() + "," +
                            perishable.getPrice() + "," +
                            perishable.getExpirationDate());
                }
            }

        } catch (IOException e) {
            System.out.println("Error while saving products: " + e.getMessage());
        }
    }
}