package org.example.laboration3.controller;

import org.example.laboration3.domain.Product;
import org.example.laboration3.service.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger =
            LoggerFactory.getLogger(ProductController.class);

    private final WarehouseService warehouseService;

    public ProductController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            List<Product> products = warehouseService.getAllProducts();
            logger.info("Hämtade {} produkter", products.size());
            return ResponseEntity.ok(products);
        } catch (RuntimeException exception) {
            logger.error("Oväntat fel vid hämtning av produkter", exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") String id) {
        try {
            Product product = warehouseService.getProductById(id);
            logger.info("Hämtade produkt med id {}", id);
            return ResponseEntity.ok(product);
        } catch (NoSuchElementException exception) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException exception) {
            logger.error("Oväntat fel vid hämtning av produkt med id {}", id, exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            Product created = warehouseService.createProduct(product);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.getId())
                    .toUri();

            logger.info("Skapade produkt med id {}", created.getId());
            return ResponseEntity.created(location).body(created);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException exception) {
            logger.error("Oväntat fel vid skapande av produkt", exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable("id") String id,
            @RequestBody Product product) {
        try {
            Product updated = warehouseService.updateProduct(id, product);
            logger.info("Uppdaterade produkt med id {}", id);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException exception) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException exception) {
            logger.error("Oväntat fel vid uppdatering av produkt med id {}", id, exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
        try {
            warehouseService.deleteProduct(id);
            logger.info("Tog bort produkt med id {}", id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException exception) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException exception) {
            logger.error("Oväntat fel vid borttagning av produkt med id {}", id, exception);
            return ResponseEntity.internalServerError().build();
        }
    }
}