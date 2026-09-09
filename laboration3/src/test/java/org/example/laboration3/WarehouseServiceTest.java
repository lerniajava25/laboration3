 package org.example.laboration3;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class WarehouseServiceTest {

    @Test
    @DisplayName ("Hämtar produkter efter kategori - en produkt i kategorin finns")
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
    @DisplayName ("Hämtar produkter efter kategori - " +
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

}
