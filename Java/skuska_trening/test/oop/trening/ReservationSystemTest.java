package oop.trening;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReservationSystemTest {
    private ReservationSystem system;
    private HotelReservation hotel;
    private FlightReservation flight;

    @BeforeEach
    void setUp() {
        system = new ReservationSystem();
        hotel = new HotelReservation("H02", "Adam", 200.0, 2);
        flight = new FlightReservation("F01", "Bea", 120.0, "economy");
    }

    @Test
    void reservationAttributesArePrivate() {
        for (Field field : Reservation.class.getDeclaredFields()) {
            assertTrue(Modifier.isPrivate(field.getModifiers()), "Reservation musi mat len private atributy");
        }
    }

    @Test
    void reservationConstructorAndGetters() {
        assertEquals("H02", hotel.getCode());
        assertEquals("Adam", hotel.getCustomerName());
        assertEquals(200.0, hotel.getPrice(), 0.001);
        assertEquals(2, hotel.getNights());

        assertEquals("F01", flight.getCode());
        assertEquals("Bea", flight.getCustomerName());
        assertEquals(120.0, flight.getPrice(), 0.001);
        assertEquals("economy", flight.getSeatClass());
    }

    @Test
    void invalidPriceThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new HotelReservation("BAD", "Nobody", -5.0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new FlightReservation("BAD2", "Nobody", 0.0, "economy"));
    }

    @Test
    void comparableAndEqualityUseCode() {
        Reservation a = new HotelReservation("A01", "A", 100.0, 1);
        Reservation b = new HotelReservation("B01", "B", 100.0, 1);
        Reservation aCopy = new FlightReservation("A01", "Other", 300.0, "business");

        assertTrue(a.compareTo(b) < 0);
        assertEquals(0, a.compareTo(aCopy));
        assertEquals(a, aCopy);
        assertEquals(a.hashCode(), aCopy.hashCode());
    }

    @Test
    void flightUpgradeWorks() {
        flight.upgrade();
        assertEquals("business", flight.getSeatClass());
        assertEquals(270.0, flight.getPrice(), 0.001);

        flight.upgrade();
        assertEquals("first", flight.getSeatClass());
        assertEquals(570.0, flight.getPrice(), 0.001);

        flight.upgrade();
        assertEquals("first", flight.getSeatClass());
        assertEquals(570.0, flight.getPrice(), 0.001);
    }

    @Test
    void systemUsesOneMap() {
        long mapCount = java.util.Arrays.stream(ReservationSystem.class.getDeclaredFields())
                .filter(field -> Map.class.isAssignableFrom(field.getType()))
                .count();
        assertEquals(1, mapCount, "ReservationSystem ma mat prave jednu mapu rezervacii");
    }

    @Test
    void addRemoveAndDuplicate() throws Exception {
        system.addReservation(hotel);
        system.addReservation(flight);
        assertEquals(2, system.getReservationCount());
        assertEquals(hotel, system.getReservation("H02"));

        assertThrows(DuplicateReservationException.class, () -> system.addReservation(
                new HotelReservation("H02", "Other", 300.0, 3)));

        system.removeReservation("H02");
        assertEquals(1, system.getReservationCount());
        assertNull(system.getReservation("H02"));
        assertThrows(ReservationNotFoundException.class, () -> system.removeReservation("XXX"));
    }

    @Test
    void sortingFilteringTotalAndGenericType() throws Exception {
        system.addReservation(hotel);
        system.addReservation(flight);
        system.addReservation(new HotelReservation("A01", "Cyril", 80.0, 1));

        List<Reservation> sorted = system.getReservationsSortedByCode();
        assertEquals("A01", sorted.get(0).getCode());
        assertEquals("F01", sorted.get(1).getCode());
        assertEquals("H02", sorted.get(2).getCode());

        List<Reservation> cheap = system.findReservationsByPriceRange(70.0, 130.0);
        assertEquals(2, cheap.size());

        assertEquals(400.0, system.calculateTotalPrice(), 0.001);

        List<HotelReservation> hotels = system.getReservationsByType(HotelReservation.class);
        assertEquals(2, hotels.size());
    }
}
