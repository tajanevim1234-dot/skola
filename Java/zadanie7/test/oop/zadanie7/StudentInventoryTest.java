package oop.zadanie7;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Študentské testy – základný sandbox na overenie tvojej implementácie.
 * Ak ti tieto testy neprechádzajú, tvoj kód určite nedostane plný počet bodov.
 * Tieto testy pokrývajú len základné scenáre. Finálne hodnotenie prebieha
 * cez TeacherEvaluationTest, ktorý je oveľa prísnejší.
 */
public class StudentInventoryTest {

    private Warehouse warehouse;
    private ElectronicProduct tv;
    private PerishableProduct apple;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        tv = new ElectronicProduct("E01", "TV Samsung", 500.0, 24);
        apple = new PerishableProduct("P01", "Apple", 1.5, LocalDate.of(2025, 12, 31));
    }

    // === WAREHOUSE ZÁKLADY ===

    @Test
    void testAddProductAndGetCount() throws Exception {
        warehouse.addProduct(tv);
        assertEquals(1, warehouse.getProductCount());
    }

    @Test
    void testAddDuplicateProductThrowsException() throws Exception {
        warehouse.addProduct(tv);
        assertThrows(ProductAlreadyExistsException.class, () -> warehouse.addProduct(tv));
    }

    @Test
    void testRemoveProduct() throws Exception {
        warehouse.addProduct(tv);
        warehouse.removeProduct("E01");
        assertEquals(0, warehouse.getProductCount());
    }

    @Test
    void testRemoveNonExistentThrows() {
        assertThrows(ProductNotFoundException.class, () -> warehouse.removeProduct("FAKE"));
    }

    // === POKROČILÉ OPERÁCIE ===

    @Test
    void testTotalValue() throws Exception {
        warehouse.addProduct(tv);
        warehouse.addProduct(apple);
        assertEquals(501.5, warehouse.calculateTotalValue(), 0.01);
    }

    @Test
    void testSortByPrice() throws Exception {
        warehouse.addProduct(tv);
        warehouse.addProduct(apple);
        List<Product> sorted = warehouse.getProductsSortedByPrice();
        assertEquals("P01", sorted.get(0).getId(), "Lacnejší produkt by mal byť prvý");
        assertEquals("E01", sorted.get(1).getId());
    }

    @Test
    void testFindByPriceRange() throws Exception {
        warehouse.addProduct(tv);
        warehouse.addProduct(apple);
        List<Product> found = warehouse.findProductsByPriceRange(1.0, 10.0);
        assertEquals(1, found.size());
        assertEquals("P01", found.get(0).getId());
    }

    // === DISCOUNTABLE ===

    @Test
    void testApplyDiscount() {
        tv.applyDiscount(10); // 10% zľava z 500 = 450
        assertEquals(450.0, tv.getPrice(), 0.01);
    }

    @Test
    void testDiscountableInterface() {
        assertTrue(tv instanceof Discountable, "ElectronicProduct musí implementovať Discountable");
    }

    // === PERZISTENCIA ===

    @Test
    void testSaveAndLoad() throws Exception {
        WarehouseLoader loader = new WarehouseLoader();
        loader.saveProducts(List.of(tv, apple), "student_test.csv");

        List<Product> loaded = loader.loadProducts("student_test.csv");
        assertEquals(2, loaded.size());

        // Cleanup
        new File("student_test.csv").delete();
    }

    @Test
    void testLoadCorruptedFileThrows() throws Exception {
        try (java.io.PrintWriter pw = new java.io.PrintWriter("student_corrupt.csv")) {
            pw.println("BAD,DATA");
        }
        WarehouseLoader loader = new WarehouseLoader();
        assertThrows(InvalidProductFormatException.class, () -> loader.loadProducts("student_corrupt.csv"));

        new File("student_corrupt.csv").delete();
    }



    // === IS EXPIRED ===

    @Test
    void testIsExpiredPast() {
        PerishableProduct expired = new PerishableProduct("P02", "OldMilk", 1.0, LocalDate.of(2020, 1, 1));
        assertTrue(expired.isExpired(), "Produkt s dátumom v minulosti musí byť expirovaný");
    }

    @Test
    void testIsExpiredFuture() {
        PerishableProduct fresh = new PerishableProduct("P03", "FreshMilk", 1.0, LocalDate.of(2099, 1, 1));
        assertFalse(fresh.isExpired(), "Produkt s dátumom v budúcnosti nesmie byť expirovaný");
    }

    // === SORTED BY ID ===

    @Test
    void testGetProductsSortedById() throws Exception {
        warehouse.addProduct(new ElectronicProduct("Z99", "Last", 100.0, 12));
        warehouse.addProduct(new ElectronicProduct("A01", "First", 200.0, 12));
        List<Product> sorted = warehouse.getProductsSortedById();
        assertEquals("A01", sorted.get(0).getId(), "Produkt s ID 'A01' musí byť prvý");
        assertEquals("Z99", sorted.get(1).getId());
    }

    // === GET PRODUCTS BY TYPE ===

    @Test
    void testGetProductsByType() throws Exception {
        warehouse.addProduct(tv);
        warehouse.addProduct(apple);
        List<ElectronicProduct> electronics = warehouse.getProductsByType(ElectronicProduct.class);
        assertEquals(1, electronics.size());
        assertEquals("E01", electronics.get(0).getId());
    }

    // === VALIDÁCIA CENY ===

    @Test
    void testNegativePriceThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElectronicProduct("E99", "Bad", -1.0, 12),
                "Záporná cena musí vyhodiť IllegalArgumentException");
    }

    // === LOAD VRÁTI PRODUKTY ZORADENÉ PODĽA ID ===

    @Test
    void testLoadReturnsSortedById() throws Exception {
        WarehouseLoader loader = new WarehouseLoader();
        // Uložíme P01 pred E01 – po načítaní musia byť zoradené E01, P01
        loader.saveProducts(List.of(apple, tv), "student_sorted.csv");
        List<Product> loaded = loader.loadProducts("student_sorted.csv");
        assertEquals("E01", loaded.get(0).getId(), "Po načítaní musí byť E01 prvý (abecedne menší)");
        assertEquals("P01", loaded.get(1).getId());
        new File("student_sorted.csv").delete();
    }

    // === FIND PRODUCT WITH SMALLEST ID (priame použitie compareTo) ===

    @Test
    void testFindProductWithSmallestId() throws Exception {
        warehouse.addProduct(new ElectronicProduct("Z99", "Last", 100.0, 12));
        warehouse.addProduct(new ElectronicProduct("A01", "First", 200.0, 12));
        warehouse.addProduct(tv); // E01
        Product smallest = warehouse.findProductWithSmallestId();
        assertEquals("A01", smallest.getId(), "Najmenšie ID abecedne musí byť A01");
    }

    @Test
    void testFindProductWithSmallestIdEmptyWarehouse() {
        assertNull(warehouse.findProductWithSmallestId(), "Prázdny sklad musí vrátiť null");
    }
}
