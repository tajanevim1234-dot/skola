package test;

public class DuplicateReservationException extends Exception {
    public DuplicateReservationException(String message){
        super(message);
    }
}