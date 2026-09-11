package org.example.laboration3;

import org.example.laboration3.domain.Product;
import org.example.laboration3.service.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * Testar följande:
- CRUD - getAllProducts()
- CRUD - getProductById()
- CRUD - createProduct() / addProduct()
- CRUD - updateProduct()
- CRUD - deleteProduct()

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

    /*
     addProductBypassingValidation används för att göra testerna 100% branch-täckande:
     Lägger till en produkt direkt i lagret, kringgår den vanliga valideringen.
     Används bara för att kunna testa null-kategori, som annars inte går
     via addProduct/createProduct.
     */

    @SuppressWarnings("unchecked")
    private void addProductBypassingValidation(Product product) {
        try {
            Field productsField = WarehouseService.class.getDeclaredField("products");
            productsField.setAccessible(true);
            Map<String, Product> internalProducts =
                    (Map<String, Product>) productsField.get(service);
            internalProducts.put(product.getId(), product);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(
                    "Kunde inte lägga till produkt direkt i lagret via reflection", e);
        }
    }

    //CRUD - getAllProducts()

    @Test
    @DisplayName("Hämtar alla produkter - lagret är tomt ger tom lista")
    void getAllProducts_whenWarehouseIsEmpty_returnsEmptyList() {

        List<Product> result = service.getAllProducts();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Hämtar alla produkter - flera produkter finns")
    void getAllProducts_whenMultipleProductsExist_returnsAllProducts() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        service.addProduct(new Product("2", "Macbook", "Datorer",
                25000, 1, LocalDate.now()));

        List<Product> result = service.getAllProducts();

        assertThat(result)
                .hasSize(2)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Iphone", "Macbook");
    }

    //CRUD - getProductById()

    @Test
    @DisplayName("Hämtar produkt via id - produkten finns")
    void getProductById_whenProductExists_returnsProduct() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        Product result = service.getProductById("1");

        assertThat(result.getName()).isEqualTo("Iphone");
    }

    @Test
    @DisplayName("Hämtar produkt via id - produkten finns inte ger exception")
    void getProductById_whenProductDoesNotExist_throwsException() {

        assertThatThrownBy(() -> service.getProductById("produktSomInteFinns"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Produkten hittades inte: produktSomInteFinns");
    }

    @Test
    @DisplayName("Hämtar produkt via id - exception om id är null")
    void getProductById_whenIdIsNull_throwsException() {

        assertThatThrownBy(() -> service.getProductById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt-id måste anges");
    }

    @Test
    @DisplayName("Hämtar produkt via id - exception om id är tom string")
    void getProductById_whenIdIsBlank_throwsException() {

        assertThatThrownBy(() -> service.getProductById("   ")) //"   " Fångar upp felakriga strängar, inklusive tomma
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt-id måste anges");
    }

    //CRUD - createProduct() / addProduct()

    @Test
    @DisplayName("Skapar produkt - lyckas och returnerar den skapade produkten")
    void createProduct_whenValid_createsAndReturnsProduct() {
        Product p = new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now());

        Product result = service.createProduct(p);

        assertThat(result).isEqualTo(p);
        assertThat(service.getAllProducts()).hasSize(1);
    }

    @Test
    @DisplayName("Lägger till produkt via addProduct - lyckas")
    void addProduct_whenValid_addsProduct() {
        Product p = new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now());

        Product result = service.addProduct(p);

        assertThat(result).isEqualTo(p);
        assertThat(service.getAllProducts()).hasSize(1);
    }

    @Test
    @DisplayName("Skapar produkt - exception om produkten är null")
    void createProduct_whenProductIsNull_throwsException() {

        assertThatThrownBy(() -> service.createProduct(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt måste anges");
    }

    @Test
    @DisplayName("Skapar produkt - exception om id är null")
    void createProduct_whenIdIsNull_throwsException() {
        Product p = new Product(null, "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now());

        assertThatThrownBy(() -> service.createProduct(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt-id måste anges");
    }

    @Test
    @DisplayName("Skapar produkt - exception om id är tom string")
    void createProduct_whenIdIsBlank_throwsException() {
        Product p = new Product("   ", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now());

        assertThatThrownBy(() -> service.createProduct(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt-id måste anges");
    }

    @Test
    @DisplayName("Skapar produkt - exception om namn är null")
    void createProduct_whenNameIsNull_throwsException() {
        Product p = new Product("1", null, "Mobiltelefoner",
                20000, 2, LocalDate.now());

        assertThatThrownBy(() -> service.createProduct(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produktnamn måste anges");
    }

    @Test
    @DisplayName("Skapar produkt - exception om namn är tom string")
    void createProduct_whenNameIsBlank_throwsException() {
        Product p = new Product("1", "   ", "Mobiltelefoner",
                20000, 2, LocalDate.now());

        assertThatThrownBy(() -> service.createProduct(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produktnamn måste anges");
    }

    @Test
    @DisplayName("Skapar produkt - exception om kategori är null")
    void createProduct_whenCategoryIsNull_throwsException() {
        Product p = new Product("1", "Iphone", null,
                20000, 2, LocalDate.now());

        assertThatThrownBy(() -> service.createProduct(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kategori måste anges");
    }

    @Test
    @DisplayName("Skapar produkt - exception om kategori innehåller ogiltiga tecken")
    void createProduct_whenCategoryHasInvalidCharacters_throwsException() {
        Product p = new Product("1", "Iphone", "Mobiler@!#",
                20000, 2, LocalDate.now());

        assertThatThrownBy(() -> service.createProduct(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kategori innehåller ogiltiga tecken");
    }

    @Test
    @DisplayName("Skapar produkt - exception om priset är negativt")
    void createProduct_whenPriceIsNegative_throwsException() {
        Product p = new Product("1", "Iphone", "Mobiltelefoner",
                -1, 2, LocalDate.now());

        assertThatThrownBy(() -> service.createProduct(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pris får inte vara negativt");
    }

    @Test
    @DisplayName("Skapar produkt - exception om lagersaldo är negativt")
    void createProduct_whenStockQuantityIsNegative_throwsException() {
        Product p = new Product("1", "Iphone", "Mobiltelefoner",
                20000, -1, LocalDate.now());

        assertThatThrownBy(() -> service.createProduct(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Lagersaldo får inte vara negativt");
    }

    @Test
    @DisplayName("Skapar produkt - exception om id redan finns")
    void createProduct_whenIdAlreadyExists_throwsException() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        Product duplicate = new Product("1", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now());

        assertThatThrownBy(() -> service.createProduct(duplicate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Det finns redan en produkt med id: 1");
    }

    //CRUD - updateProduct()

    @Test
    @DisplayName("Uppdaterar produkt - lyckas och ersätter befintlig produkt")
    void updateProduct_whenProductExists_updatesProduct() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        Product updated = new Product("1", "Iphone Pro", "Mobiltelefoner",
                22000, 5, LocalDate.now());

        Product result = service.updateProduct("1", updated);

        assertThat(result.getName()).isEqualTo("Iphone Pro");
        assertThat(service.getProductById("1").getPrice()).isEqualTo(22000);
        assertThat(service.getProductById("1").getStockQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Uppdaterar produkt - id-parametern sätts på produkten oavsett vad produkten hade")
    void updateProduct_setsIdFromParameter() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        Product updated = new Product("annat-id", "Iphone Pro", "Mobiltelefoner",
                22000, 5, LocalDate.now());

        Product result = service.updateProduct("1", updated);

        assertThat(result.getId()).isEqualTo("1");
    }

    @Test
    @DisplayName("Uppdaterar produkt - exception om id är null")
    void updateProduct_whenIdIsNull_throwsException() {
        Product updated = new Product("1", "Iphone Pro", "Mobiltelefoner",
                22000, 5, LocalDate.now());

        assertThatThrownBy(() -> service.updateProduct(null, updated))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt-id måste anges");
    }

    @Test
    @DisplayName("Uppdaterar produkt - exception om id är tom string")
    void updateProduct_whenIdIsBlank_throwsException() {
        Product updated = new Product("1", "Iphone Pro", "Mobiltelefoner",
                22000, 5, LocalDate.now());

        assertThatThrownBy(() -> service.updateProduct("   ", updated))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt-id måste anges");
    }

    @Test
    @DisplayName("Uppdaterar produkt - exception om produkten är null")
    void updateProduct_whenProductIsNull_throwsException() {

        assertThatThrownBy(() -> service.updateProduct("1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt måste anges");
    }

    @Test
    @DisplayName("Uppdaterar produkt - exception om produkten inte finns")
    void updateProduct_whenProductDoesNotExist_throwsException() {
        Product updated = new Product("1", "Iphone Pro", "Mobiltelefoner",
                22000, 5, LocalDate.now());

        assertThatThrownBy(() -> service.updateProduct("produktSomInteFinns", updated))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Produkten hittades inte: produktSomInteFinns");
    }

    @Test
    @DisplayName("Uppdaterar produkt - exception om det uppdaterade priset är negativt")
    void updateProduct_whenPriceIsNegative_throwsException() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        Product updated = new Product("1", "Iphone Pro", "Mobiltelefoner",
                -1, 5, LocalDate.now());

        assertThatThrownBy(() -> service.updateProduct("1", updated))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pris får inte vara negativt");
    }

    // CRUD - deleteProduct()

    @Test
    @DisplayName("Tar bort produkt - lyckas när produkten finns")
    void deleteProduct_whenProductExists_removesProduct() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        service.deleteProduct("1");

        assertThat(service.getAllProducts()).isEmpty();
    }

    @Test
    @DisplayName("Tar bort produkt - exception om id är null")
    void deleteProduct_whenIdIsNull_throwsException() {

        assertThatThrownBy(() -> service.deleteProduct(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt-id måste anges");
    }

    @Test
    @DisplayName("Tar bort produkt - exception om id är tom string")
    void deleteProduct_whenIdIsBlank_throwsException() {

        assertThatThrownBy(() -> service.deleteProduct("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Produkt-id måste anges");
    }

    @Test
    @DisplayName("Tar bort produkt - exception om produkten inte finns")
    void deleteProduct_whenProductDoesNotExist_throwsException() {

        assertThatThrownBy(() -> service.deleteProduct("produktSomInteFinns"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Produkten hittades inte: produktSomInteFinns");
    }

    // Sök & Filtrera - getProductsByCategory()

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

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Iphone");
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

        assertThat(result).hasSize(2);
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

        assertThat(result).isEmpty();
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

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Iphone");
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - produkter utan kategori ignoreras")
    void getProductsByCategory_ignoresProductsWithNullCategory() {
        // Kan inte skapas via addProduct/createProduct eftersom validateProduct
        // förhindrar null-kategori - läggs in direkt i lagret för att täcka
        // filtret product.getCategory() != null.
        addProductBypassingValidation(new Product("1", "Iphone", null,
                20000, 2, LocalDate.now()));

        service.addProduct(new Product("2", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now()));

        List<Product> result = service.getProductsByCategory("Mobiltelefoner");

        assertThat(result)
                .hasSize(1)
                .first()
                .extracting(Product::getName)
                .isEqualTo("Samsung");
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - exception om kategori är null")
    void getProductsByCategory_whenCategoryIsNull_throwsException() {

        assertThatThrownBy(() -> service.getProductsByCategory(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kategori måste anges");
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - exception om kategori är tom string")
    void getProductsByCategory_whenCategoryIsBlank_throwsException() {

        assertThatThrownBy(() -> service.getProductsByCategory("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kategori måste anges");
    }

    @Test
    @DisplayName("Hämtar produkter efter kategori - exception vid ogiltiga tecken")
    void getProductsByCategory_whenCategoryContainsInvalidCharacters_throwsException() {

        assertThatThrownBy(() -> service.getProductsByCategory("Mobiler@!#"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kategori innehåller ogiltiga tecken");
    }

    // Analys & Aggregering - calculateTotalInventoryValue()

    @Test
    @DisplayName("Beräknar totalt lagervärde - flera produkter finns")
    void calculateTotalInventoryValue_whenMultipleProductsExist() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        service.addProduct(new Product("2", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now()));

        service.addProduct(new Product("3", "Macbook", "Datorer",
                25000, 1, LocalDate.now()));

        double result = service.calculateTotalInventoryValue();

        assertThat(result).isCloseTo(110000, within(0.001)); //felmarginal på 0.001
    }

    @Test
    @DisplayName("Beräknar totalt lagervärde - lagret är tomt ger 0")
    void calculateTotalInventoryValue_whenWarehouseIsEmpty_returnsZero() {

        double result = service.calculateTotalInventoryValue();

        assertThat(result).isCloseTo(0, within(0.001));
    }

    @Test
    @DisplayName("Beräknar totalt lagervärde - produkt med lagersaldo 0 bidrar inte till värdet")
    void calculateTotalInventoryValue_whenQuantityIsZero_contributesNothing() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 0, LocalDate.now()));

        double result = service.calculateTotalInventoryValue();

        assertThat(result).isCloseTo(0, within(0.001));
    }

    @Test
    @DisplayName("Beräknar totalt lagervärde - produkt med lagersaldo 0 räknas fortfarande med " +
            "bland flera produkter")
    void calculateTotalInventoryValue_whenOneProductHasZeroQuantity_stillIncludedInCalculation() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 0, LocalDate.now()));

        service.addProduct(new Product("2", "Samsung", "Mobiltelefoner",
                15000, 3, LocalDate.now()));

        double result = service.calculateTotalInventoryValue();

        assertThat(result).isCloseTo(45000, within(0.001));
    }

    @Test
    @DisplayName("Beräknar totalt lagervärde - hanterar decimaltal korrekt")
    void calculateTotalInventoryValue_handlesDecimalPricesCorrectly() {
        service.addProduct(new Product("1", "Laddare", "Tillbehör",
                199.95, 3, LocalDate.now()));   // 599.85

        service.addProduct(new Product("2", "Skal", "Tillbehör",
                99.50, 2, LocalDate.now()));    // 199.00

        double result = service.calculateTotalInventoryValue();

        assertThat(result).isCloseTo(798.85, within(0.001));
    }

    // Analys & Aggregering - calculateAveragePriceByCategory()

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

        assertThat(result)
                .hasSize(2)
                .containsEntry("Mobiltelefoner", 15000.0)
                .containsEntry("Datorer", 25000.0);
    }

    @Test
    @DisplayName("Beräknar medelpris per kategori - en kategori med en produkt " +
            "ger medelpris lika med produktens pris")
    void calculateAveragePriceByCategory_whenSingleProductInCategory() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        Map<String, Double> result = service.calculateAveragePriceByCategory();

        assertThat(result).containsEntry("Mobiltelefoner", 20000.0);
    }

    @Test
    @DisplayName("Beräknar medelpris per kategori - lagret är tomt ger tom Map")
    void calculateAveragePriceByCategory_whenWarehouseIsEmpty_returnsEmptyMap() {

        Map<String, Double> result = service.calculateAveragePriceByCategory();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Beräknar medelpris per kategori - produkter utan kategori ignoreras")
    void calculateAveragePriceByCategory_ignoresProductsWithNullCategory() {
        // Kan inte skapas via addProduct/createProduct eftersom validateProduct
        // förhindrar null-kategori - läggs in direkt i lagret för att täcka
        // filtret product.getCategory() != null.
        addProductBypassingValidation(new Product("1", "Iphone", null,
                20000, 2, LocalDate.now()));

        Map<String, Double> result = service.calculateAveragePriceByCategory();

        assertThat(result).isEmpty();
    }

    // Sortering - getMostExpensiveProducts()

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

        assertThat(result)
                .hasSize(2)
                .extracting(Product::getName)
                .containsExactly("Macbook", "Iphone");
    }

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - lika pris sorteras efter namn (case-insensitive)")
    void getMostExpensiveProducts_whenPricesAreEqual_sortsByNameCaseInsensitive() {
        service.addProduct(new Product("1", "samsung", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        service.addProduct(new Product("2", "Apple", "Mobiltelefoner",
                20000, 3, LocalDate.now()));

        List<Product> result = service.getMostExpensiveProducts(2);

        assertThat(result)
                .extracting(Product::getName)
                .containsExactly("Apple", "samsung");
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

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - lagret är tomt ger tom lista")
    void getMostExpensiveProducts_whenWarehouseIsEmpty_returnsEmptyList() {

        List<Product> result = service.getMostExpensiveProducts(3);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - N = 0 ger tom lista")
    void getMostExpensiveProducts_whenNIsZero_returnsEmptyList() {
        service.addProduct(new Product("1", "Iphone", "Mobiltelefoner",
                20000, 2, LocalDate.now()));

        List<Product> result = service.getMostExpensiveProducts(0);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Hämtar topp N dyraste produkter - exception om N är negativt")
    void getMostExpensiveProducts_whenNIsNegative_throwsException() {

        assertThatThrownBy(() -> service.getMostExpensiveProducts(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Antal (n) får inte vara negativt");
    }
}
