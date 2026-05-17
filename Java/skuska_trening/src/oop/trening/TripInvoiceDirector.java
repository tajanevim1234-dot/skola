package oop.trening;

public class TripInvoiceDirector {
    private InvoiceBuilderInterface builder;

    public TripInvoiceDirector(InvoiceBuilderInterface builder) {
        this.builder = builder;
    }

    public void createReservationInvoice(Reservation reservation) {
        // TODO
    }

    public void createUpgradeInvoice(FlightReservation reservation) {
        // TODO
    }
}
