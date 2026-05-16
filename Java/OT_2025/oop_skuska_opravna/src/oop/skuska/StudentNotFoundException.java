package oop.skuska;

public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(int id) {
        super("Student with id: %d not found".formatted(id));
    }
}
