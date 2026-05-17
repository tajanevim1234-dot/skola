package oop.trening;

public class TripInvoiceDirector {
    private InvoiceBuilderInterface builder;

    public TripInvoiceDirector(InvoiceBuilderInterface builder) {
        this.builder = builder;
    }

    public void createReservationInvoice(Reservation reservation) {
        builder.setCustomer(reservation.getCustomerName());
        builder.setContent("Reservation " + reservation.getCode() + " for " + reservation.getCustomerName());
        builder.setTotal(reservation.getPrice());
    }

    public void createUpgradeInvoice(FlightReservation reservation) {
        builder.setCustomer(reservation.getCustomerName());
        builder.setContent("Upgrade for flight " + reservation.getCode() + " to " + reservation.getSeatClass());
        builder.setTotal(reservation.getPrice());
    }
}
