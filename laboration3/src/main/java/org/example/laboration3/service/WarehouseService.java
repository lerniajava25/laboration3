package org.example.laboration3.service;

import org.example.laboration3.domain.Product;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class WarehouseService {

    private final Map<String, Product> products =
            new ConcurrentHashMap<>();

    // CRUD-metoder som ProductController behöver

    public List<Product> getAllProducts() {
        return List.copyOf(products.values());
    }

    public Product getProductById(String id) {
        validateId(id);

        Product product = products.get(id);

        if (product == null) {
            throw new NoSuchElementException(
                    "Produkten hittades inte: " + id
            );
        }

        return product;
    }

    public Product createProduct(Product product) {
        validateProduct(product);

        Product existingProduct =
                products.putIfAbsent(product.getId(), product);

        if (existingProduct != null) {
            throw new IllegalArgumentException(
                    "Det finns redan en produkt med id: "
                            + product.getId()
            );
        }

        return product;
    }


    public Product addProduct(Product product) {
        return createProduct(product);
    }

    public Product updateProduct(String id, Product product) {
        validateId(id);

        if (product == null) {
            throw new IllegalArgumentException(
                    "Produkt måste anges"
            );
        }

        product.setId(id);
        validateProduct(product);

        Product previousProduct =
                products.replace(id, product);

        if (previousProduct == null) {
            throw new NoSuchElementException(
                    "Produkten hittades inte: " + id
            );
        }

        return product;
    }

    public void deleteProduct(String id) {
        validateId(id);

        Product removedProduct = products.remove(id);

        if (removedProduct == null) {
            throw new NoSuchElementException(
                    "Produkten hittades inte: " + id
            );
        }
    }

    // Collections och Java Streams

    public List<Product> getProductsByCategory(String category) {
        validateCategory(category);

        String searchedCategory = category.trim();

        return products.values().stream()
                .filter(product -> product.getCategory() != null)
                .filter(product ->
                        product.getCategory()
                                .equalsIgnoreCase(searchedCategory))
                .toList();
    }

    public double calculateTotalInventoryValue() {
        return products.values().stream()
                .mapToDouble(product ->
                        product.getPrice()
                                * product.getStockQuantity())
                .sum();
    }

    public Map<String, Double> calculateAveragePriceByCategory() {
        return products.values().stream()
                .filter(product -> product.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingDouble(Product::getPrice)
                ));
    }

    public List<Product> getMostExpensiveProducts(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(
                    "Antal (n) får inte vara negativt"
            );
        }

        return products.values().stream()
                .sorted(
                        Comparator.comparingDouble(Product::getPrice)
                                .reversed()
                                .thenComparing(
                                        Product::getName,
                                        String.CASE_INSENSITIVE_ORDER
                                )
                )
                .limit(n)
                .toList();
    }

    // Valideringsmetoder

    private void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Produkt-id måste anges"
            );
        }
    }

    private void validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Kategori måste anges"
            );
        }

        if (!category.trim().matches("[\\p{L}\\p{N} _-]+")) {
            throw new IllegalArgumentException(
                    "Kategori innehåller ogiltiga tecken"
            );
        }
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "Produkt måste anges"
            );
        }

        validateId(product.getId());

        if (product.getName() == null
                || product.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Produktnamn måste anges"
            );
        }

        validateCategory(product.getCategory());

        if (product.getPrice() < 0) {
            throw new IllegalArgumentException(
                    "Pris får inte vara negativt"
            );
        }

        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Lagersaldo får inte vara negativt"
            );
        }
    }
}