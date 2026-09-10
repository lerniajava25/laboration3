package org.example.laboration3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/* Testar följande:
- Sök & Filtrera - getProductsByCategory()
- Analys & Aggregering - calculateTotalInventoryValue()
- Analys & Aggregering - calculateAveragePriceByCategory()
- Sortering - getMostExpensiveProducts()
 */

class WarehouseServiceTest {

    private WarehouseService service;

    @BeforeEach
    void setUp() {
        service = new WarehouseService();
    }

    //Sök & Filtrera - getProductsByCategory()

    @Test
    @DisplayName("Hämtar produkter efter kategori - en produkt i kategorin finns")
    void getProductsByCategory_whenOneProductsExist() {
        Product p = new Product(
                "1",
                "Iphone",
                "Mobiltelefoner",
                20000,
                2,
                LocalDate.now()
        );

        service.addProduct(p);

        List<Product> result = service.getProductsByCategory("Mobiltelefoner");

        assertEquals(1, result.size());
        assertEquals("Iphone", result.get(0).getName());
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - " +
            "flera produkter, med olika kategorier finns")
    void getProductsByCategory_whenMultipleProductsExist() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        service.addProduct(new Product("2", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now()));

        service.addProduct(new Product("3", "Macbook", "Datorer",
                25000, 1, LocalDate.now()));

        List<Product> result = service.getProductsByCategory("Mobiltelefoner");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - kategori finns inte")
    void getProductsByCategory_whenCategoryDoesNotExist_returnsEmptyList() {

        service.addProduct(new Product(
                "1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()
        ));

        List<Product> result =
                service.getProductsByCategory("Datorer");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - sökningen är inte känslig för versaler")
    void getProductsByCategory_isCaseInsensitive() {

        service.addProduct(new Product(
                "1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()
        ));

        List<Product> result =
                service.getProductsByCategory("mobilTelefoner");

        assertEquals(1, result.size());
        assertEquals("Iphone", result.get(0).getName());
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - exception om kategori är null")
    void getProductsByCategory_whenCategoryIsNull_throwsException() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getProductsByCategory(null));

        assertEquals("Kategori måste anges", exception.getMessage());
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - exception om kategori är tom sträng")
    void getProductsByCategory_whenCategoryIsBlank_throwsException() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getProductsByCategory("   "));

        assertEquals("Kategori måste anges", exception.getMessage());
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - exception vid ogiltiga tecken")
    void getProductsByCategory_whenCategoryContainsInvalidCharacters_throwsException() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getProductsByCategory("Mobiler@!#"));

        assertEquals("Kategori innehåller ogiltiga tecken", exception.getMessage());
    }

    //Analys & Aggregering - calculateTotalInventoryValue()

    @Test
    @DisplayName("Beräknar totalt lagervärde - flera produkter finns")
    void calculateTotalInventoryValue_whenMultipleProductsExist() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));   // 40 000

        service.addProduct(new Product("2", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now()));   // 45 000

        service.addProduct(new Product("3", "Macbook", "Datorer",
                25000, 1, LocalDate.now()));   // 25 000

        double result = service.calculateTotalInventoryValue();

        assertEquals(110000, result, 0.001);
    }

    @Test
    @DisplayName("Beräknar totalt lagervärde - lagret är tomt ger 0")
    void calculateTotalInventoryValue_whenWarehouseIsEmpty_returnsZero() {

        double result = service.calculateTotalInventoryValue();

        assertEquals(0, result, 0.001);
    }

    @Test
    @DisplayName("Beräknar totalt lagervärde - produkt med lagersaldo 0 bidrar inte till värdet")
    void calculateTotalInventoryValue_whenQuantityIsZero_contributesNothing() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 0, LocalDate.now()));

        double result = service.calculateTotalInventoryValue();

        assertEquals(0, result, 0.001);
    }

    @Test
    @DisplayName("Beräknar totalt lagervärde - produkt med lagersaldo 0 räknas fortfarande med " +
            "bland flera produkter")
    void calculateTotalInventoryValue_whenOneProductHasZeroQuantity_stillIncludedInCalculation() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 0, LocalDate.now()));   // 0

        service.addProduct(new Product("2", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now()));   // 45 000

        double result = service.calculateTotalInventoryValue();

        assertEquals(45000, result, 0.001);
    }

    @Test
    @DisplayName("Beräknar totalt lagervärde - hanterar decimaltal korrekt")
    void calculateTotalInventoryValue_handlesDecimalPricesCorrectly() {
        service.addProduct(new Product("1", "Laddare", "Tillbehör",
                199.95, 3, LocalDate.now()));   // 599.85

        service.addProduct(new Product("2", "Skal", "Tillbehör",
                99.50, 2, LocalDate.now()));    // 199.00

        double result = service.calculateTotalInventoryValue();

        assertEquals(798.85, result, 0.001);
    }

    //Analys & Aggregering - calculateAveragePriceByCategory()

    @Test
    @DisplayName("Beräknar medelpris per kategori - flera kategorier finns")
    void calculateAveragePriceByCategory_whenMultipleCategoriesExist() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        service.addProduct(new Product("2", "Samsung", "Mobiltelefoner",
                10000, 3, LocalDate.now()));

        service.addProduct(new Product("3", "Macbook", "Datorer",
                25000, 1, LocalDate.now()));

        Map<String, Double> result = service.calculateAveragePriceByCategory();

        assertEquals(2, result.size());
        assertEquals(15000, result.get("Mobiltelefoner"), 0.001);
        assertEquals(25000, result.get("Datorer"), 0.001);
    }

    @Test
    @DisplayName("Beräknar medelpris per kategori - en kategori med en produkt " +
            "ger medelpris lika med produktens pris")
    void calculateAveragePriceByCategory_whenSingleProductInCategory() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        Map<String, Double> result = service.calculateAveragePriceByCategory();

        assertEquals(20000, result.get("Mobiltelefoner"), 0.001);
    }

    @Test
    @DisplayName("Beräknar medelpris per kategori - lagret är tomt ger tom Map")
    void calculateAveragePriceByCategory_whenWarehouseIsEmpty_returnsEmptyMap() {

        Map<String, Double> result = service.calculateAveragePriceByCategory();

        assertTrue(result.isEmpty());
    }

    //Sortering - getMostExpensiveProducts()

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - returnerar rätt antal i fallande prisordning")
    void getMostExpensiveProducts_returnsProductsSortedByPriceDescending() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        service.addProduct(new Product("2", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now()));

        service.addProduct(new Product("3", "Macbook", "Datorer",
                25000, 1, LocalDate.now()));

        List<Product> result = service.getMostExpensiveProducts(2);

        assertEquals(2, result.size());
        assertEquals("Macbook", result.get(0).getName());
        assertEquals("Iphone", result.get(1).getName());
    }

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - N är större än antalet produkter " +
            "ger samtliga produkter")
    void getMostExpensiveProducts_whenNGreaterThanNumberOfProducts_returnsAll() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        service.addProduct(new Product("2", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now()));

        List<Product> result = service.getMostExpensiveProducts(10);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - lagret är tomt ger tom lista")
    void getMostExpensiveProducts_whenWarehouseIsEmpty_returnsEmptyList() {

        List<Product> result = service.getMostExpensiveProducts(3);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - N = 0 ger tom lista")
    void getMostExpensiveProducts_whenNIsZero_returnsEmptyList() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        List<Product> result = service.getMostExpensiveProducts(0);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - kastar exception om N är negativt")
    void getMostExpensiveProducts_whenNIsNegative_throwsException() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getMostExpensiveProducts(-1));

        assertEquals("Antal (n) får inte vara negativt", exception.getMessage());
    }

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - lika pris ger alfabetisk sekundärsortering")
    void getMostExpensiveProducts_whenPricesAreEqual_sortsAlphabeticallyByName() {
        service.addProduct(new Product("1", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now()));

        service.addProduct(new Product("2", "Iphone", "Mobiltelefoner",
                15000, 2, LocalDate.now()));

        service.addProduct(new Product("3", "Huawei", "Mobiltelefoner",
                15000, 1, LocalDate.now()));

        List<Product> result = service.getMostExpensiveProducts(3);

        assertEquals(3, result.size());
        assertEquals("Huawei", result.get(0).getName());
        assertEquals("Iphone", result.get(1).getName());
        assertEquals("Samsung", result.get(2).getName());
    }
}
