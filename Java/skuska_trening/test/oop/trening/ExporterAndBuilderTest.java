package oop.trening;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class ExporterAndBuilderTest {
    @Test
    void csvExporterOneAndMany() {
        ReservationExporter exporter = new CsvReservationExporter();
        Reservation r1 = new HotelReservation("A01", "Adam", 100.0, 1);
        Reservation r2 = new FlightReservation("B01", "Bea", 250.0, "business");

        assertEquals("""
                A01; Adam; 100.0
                """, exporter.export(r1));

        Set<Reservation> reservations = new TreeSet<>();
        reservations.add(r2);
        reservations.add(r1);

        assertEquals("""
                code; customer; price
                A01; Adam; 100.0
                B01; Bea; 250.0
                """, exporter.export(reservations));
    }

    @Test
    void textExporterMany() {
        ReservationExporter exporter = new TextReservationExporter();
        Set<Reservation> reservations = new TreeSet<>();
        reservations.add(new FlightReservation("B01", "Bea", 250.0, "business"));
        reservations.add(new HotelReservation("A01", "Adam", 100.0, 1));

        assertEquals("""
                [A01] Adam - 100.0
                [B01] Bea - 250.0
                """, exporter.export(reservations));
    }

    @Test
    void systemSwitchesExporter() throws Exception {
        ReservationSystem system = new ReservationSystem();
        system.addReservation(new HotelReservation("A01", "Adam", 100.0, 1));

        system.setExporter(new CsvReservationExporter());
        assertEquals("""
                code; customer; price
                A01; Adam; 100.0
                """, system.exportReservations());

        system.setExporter(new TextReservationExporter());
        assertEquals("""
                [A01] Adam - 100.0
                """, system.exportReservations());
    }

    @Test
    void invoiceStructure() {
        for (Field field : Invoice.class.getDeclaredFields()) {
            assertTrue(Modifier.isPrivate(field.getModifiers()), "Invoice musi mat len private atributy");
        }

        Constructor<?>[] constructors = Invoice.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        int modifier = constructors[0].getModifiers();
        assertFalse(Modifier.isPrivate(modifier));
        assertFalse(Modifier.isProtected(modifier));
        assertFalse(Modifier.isPublic(modifier), "Invoice konstruktor ma byt package-private");
    }

    @Test
    void invoiceBuilderBuildsAndResets() throws Exception {
        InvoiceBuilderInterface builder = new InvoiceBuilder();
        builder.setCustomer("Adam");
        builder.setContent("Reservation A01");
        builder.setTotal(100.0);

        Invoice invoice = builder.build();
        assertEquals("Adam", invoice.getCustomer());
        assertEquals("Reservation A01", invoice.getContent());
        assertEquals(100.0, invoice.getTotal(), 0.001);

        assertThrows(InvoiceNotBuildableException.class, builder::build,
                "Po uspesnom build() sa builder musi resetnut");
    }

    @Test
    void invoiceBuilderThrowsWhenIncomplete() {
        InvoiceBuilderInterface builder = new InvoiceBuilder();
        assertThrows(InvoiceNotBuildableException.class, builder::build);

        builder.setCustomer("Adam");
        builder.setTotal(100.0);
        assertThrows(InvoiceNotBuildableException.class, builder::build);

        builder.reset();
        builder.setCustomer("Adam");
        builder.setContent("Reservation A01");
        builder.setTotal(0.0);
        assertThrows(InvoiceNotBuildableException.class, builder::build);
    }

    @Test
    void directorUsesInterfaceAndCreatesContent() throws Exception {
        for (Field field : TripInvoiceDirector.class.getDeclaredFields()) {
            assertNotEquals(InvoiceBuilder.class, field.getType(),
                    "Director ma pouzivat InvoiceBuilderInterface, nie konkretny InvoiceBuilder");
        }

        InvoiceBuilderInterface builder = new InvoiceBuilder();
        TripInvoiceDirector director = new TripInvoiceDirector(builder);
        Reservation reservation = new HotelReservation("H02", "Adam", 200.0, 2);

        director.createReservationInvoice(reservation);
        Invoice invoice = builder.build();

        assertEquals("Adam", invoice.getCustomer());
        assertEquals("Reservation H02 for Adam", invoice.getContent());
        assertEquals(200.0, invoice.getTotal(), 0.001);

        FlightReservation flight = new FlightReservation("F01", "Bea", 120.0, "business");
        director.createUpgradeInvoice(flight);
        Invoice upgrade = builder.build();

        assertEquals("Bea", upgrade.getCustomer());
        assertEquals("Upgrade for flight F01 to business", upgrade.getContent());
        assertEquals(120.0, upgrade.getTotal(), 0.001);
    }
}
