package oop.zadanie6;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudentInventoryTest {

    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
    }

    // === 1. Vytvorenie a gettery PerishableProduct ===
    @Test
    void testPerishableProductCreationAndGetters() {
        PerishableProduct p = new PerishableProduct("P1", "Milk", 1.5, 5);
        assertEquals("P1", p.getId());
        assertEquals("Milk", p.getName());
        assertEquals(1.5, p.getPrice());
        assertEquals(5, p.getExpirationDays());
    }

    // === 2. Vytvorenie a gettery ElectronicProduct ===
    @Test
    void testElectronicProductCreationAndGetters() {
        ElectronicProduct ep = new ElectronicProduct("E1", "Laptop", 1000.0, 24);
        assertEquals("E1", ep.getId());
        assertEquals("Laptop", ep.getName());
        assertEquals(1000.0, ep.getPrice());
        assertEquals(24, ep.getWarrantyMonths());
    }

    // === 3. Settery – zmena ceny, expirationDays, warrantyMonths ===
    @Test
    void testSetters() {
        PerishableProduct pp = new PerishableProduct("P1", "Milk", 1.5, 5);
        pp.setPrice(2.0);
        assertEquals(2.0, pp.getPrice());
        pp.setExpirationDays(10);
        assertEquals(10, pp.getExpirationDays());

        ElectronicProduct ep = new ElectronicProduct("E1", "TV", 500.0, 12);
        ep.setWarrantyMonths(36);
        assertEquals(36, ep.getWarrantyMonths());
    }

    // === 4. Rovnosť produktov (equals) – rovnaké ID = rovnaké produkty ===
    @Test
    void testProductEqualitySameId() {
        Product p1 = new PerishableProduct("ID1", "Apple", 0.5, 7);
        Product p2 = new PerishableProduct("ID1", "Different Apple", 0.6, 14);
        assertEquals(p1, p2, "Produkty s rovnakým ID musia byť rovnaké.");
    }

    // === 5. Rovnosť produktov – rôzne ID = rôzne produkty ===
    @Test
    void testProductEqualityDifferentId() {
        Product p1 = new PerishableProduct("ID1", "Apple", 0.5, 7);
        Product p2 = new PerishableProduct("ID2", "Apple", 0.5, 7);
        assertNotEquals(p1, p2, "Produkty s rôznym ID nesmú byť rovnaké.");
    }

    // === 6. toString musí obsahovať kľúčové údaje ===
    @Test
    void testToStringContainsInfo() {
        PerishableProduct pp = new PerishableProduct("P1", "Bread", 1.2, 3);
        String s = pp.toString();
        assertTrue(s.contains("P1"), "toString musí obsahovať ID.");
        assertTrue(s.contains("Bread"), "toString musí obsahovať meno.");
        assertTrue(s.matches(".*1[\\.,]2.*"), "toString musí obsahovať CENU.");
    }

    // === 7. Warehouse – pridanie a počet ===
    @Test
    void testWarehouseAddAndCount() throws ProductAlreadyExistsException {
        warehouse.addProduct(new PerishableProduct("P1", "A", 1.0, 1));
        warehouse.addProduct(new ElectronicProduct("E1", "B", 2.0, 12));
        assertEquals(2, warehouse.getProductCount());
    }

    // === 8. Warehouse – úspešné odobranie produktu ===
    @Test
    void testWarehouseRemoveProduct() throws ProductAlreadyExistsException {
        warehouse.addProduct(new PerishableProduct("P1", "Milk", 1.5, 5));
        assertDoesNotThrow(() -> warehouse.removeProduct("P1"));
        assertEquals(0, warehouse.getProductCount());
    }

    // === 9. Warehouse – výnimka pri odobraní neexistujúceho produktu ===
    @Test
    void testWarehouseRemoveThrowsException() {
        assertThrows(ProductNotFoundException.class,
                () -> warehouse.removeProduct("NEEXISTUJE"),
                "Odobranie neexistujúceho produktu musí vyhodiť ProductNotFoundException.");
    }

    // === 10. Zľava – platná a neplatná ===
    @Test
    void testDiscountApply() {
        ElectronicProduct ep = new ElectronicProduct("E1", "Laptop", 1000.0, 24);

        // Platná zľava 10%
        ep.applyDiscount(10);
        assertEquals(900.0, ep.getPrice(), 0.001, "10% zľava z 1000 = 900.");

        // Neplatná zľava (mimo rozsahu (0, 100>) – cena sa nemení
        ep.applyDiscount(150);
        assertEquals(900.0, ep.getPrice(), "Neplatná zľava sa musí ignorovať.");
    }

    // === 11. Warehouse – výnimka pri pridaní duplicitného produktu ===
    @Test
    void testWarehouseAddDuplicateThrowsException() throws ProductAlreadyExistsException {
        warehouse.addProduct(new PerishableProduct("P1", "Milk", 1.5, 5));
        assertThrows(ProductAlreadyExistsException.class,
                () -> warehouse.addProduct(new PerishableProduct("P1", "Iné mlieko", 2.0, 3)),
                "Pridanie produktu s rovnakým ID musí vyhodiť ProductAlreadyExistsException.");
    }

    // === 12. Warehouse – Set nepridá duplicitný produkt ===
    @Test
    void testWarehouseDuplicateDoesNotIncreaseCount() throws ProductAlreadyExistsException {
        warehouse.addProduct(new PerishableProduct("P1", "Milk", 1.5, 5));
        try {
            warehouse.addProduct(new PerishableProduct("P1", "Iné mlieko", 2.0, 3));
        } catch (ProductAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(1, warehouse.getProductCount(), "Po neúspešnom pridaní duplicity musí byť stále 1 produkt.");
    }
}
