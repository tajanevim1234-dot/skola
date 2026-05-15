package oop.zadanie6;

public class ProductAlreadyExistsException extends Exception {
    
    public ProductAlreadyExistsException(String message){
        super("Product s ID '" + message + "' sa v sklade už nachádza.");
    }

}
