package com.example.product_api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.product_api.model.Product;
import com.example.product_api.repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * GET /products
     * Retourne la liste de tous les produits.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        List<Product> products = repository.findAll();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /products/{id}
     * Retourne un produit par son id.
     * Lance une exception 404 si le produit n'existe pas.
     */
    @GetMapping("/{id:\\d+}") // Regex pour s'assurer que id est un nombre
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Produit non trouvé avec l'id : " + id));
        return ResponseEntity.ok(product);
    }

    /**
     * POST /products
     * Crée un nouveau produit avec les données du corps de la requête.
     */
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product savedProduct = repository.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    /**
     * PUT /products/{id}
     * Met à jour un produit existant.
     * Lance une exception 404 si le produit n'existe pas.
     */
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        Product existing = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Produit non trouvé avec l'id : " + id));

        existing.setName(product.getName());
        existing.setPrice(product.getPrice());

        Product updated = repository.save(existing);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /products/{id}
     * Supprime un produit par son id.
     */
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Produit non trouvé avec l'id : " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /products/bundle
     * Crée un bundle de produits à partir d'une liste d'ids de produits sources.
     * Vérifie que tous les produits sources existent et qu'il n'y a pas de boucle.
     */
    @PostMapping("/bundle")
    public ResponseEntity<Product> createBundle(@RequestBody List<Long> sourceIds) {
        List<Product> sources = repository.findAllById(sourceIds);

        if (sources.size() != sourceIds.size()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Certains produits sources n'ont pas été trouvés");
        }

        if (hasRecursion(sources, sources)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Boucle détectée dans les produits sources");
        }

        String bundleName = sources.stream()
                                   .map(Product::getName)
                                   .collect(Collectors.joining(" + "));

        double totalPrice = sources.stream()
                                   .mapToDouble(Product::getPrice)
                                   .sum();

        Product bundle = new Product();
        bundle.setName(bundleName);
        bundle.setPrice(totalPrice);
        bundle.setSources(sources);

        Product savedBundle = repository.save(bundle);
        return new ResponseEntity<>(savedBundle, HttpStatus.CREATED);
    }

    /**
     * Vérifie récursivement si une boucle existe dans les produits sources.
     * @param productsToCheck la liste de produits à vérifier
     * @param originalSources la liste originale des sources pour détecter la boucle
     * @return true s'il y a une boucle, false sinon
     */
    private boolean hasRecursion(List<Product> productsToCheck, List<Product> originalSources) {
        for (Product product : productsToCheck) {
            List<Product> sources = product.getSources();
            if (sources != null && !sources.isEmpty()) {
                for (Product source : sources) {
                    if (originalSources.contains(source)) {
                        return true; // boucle détectée
                    }
                    if (hasRecursion(List.of(source), originalSources)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
