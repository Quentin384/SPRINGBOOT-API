package com.example.productapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;

@RestController // Indique que cette classe est un contrôleur REST, répondant aux requêtes HTTP
@RequestMapping("/products") // Base de l’URL pour toutes les méthodes dans ce contrôleur
public class ProductController {

    // Injection du repository pour accéder aux données en base
    private final ProductRepository repository;

    // Constructeur avec injection du repository
    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    // GET /products
    // Cette méthode retourne la liste de tous les produits en base
    @GetMapping
    public List<Product> getAll() {
        return repository.findAll(); // On récupère tous les produits via JpaRepository
    }

    // GET /products/{id}
    // Retourne un produit précis identifié par son id dans l'URL
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        // findById renvoie un Optional, orElseThrow lance une exception si pas trouvé
        return repository.findById(id).orElseThrow(() -> 
            new RuntimeException("Produit non trouvé avec l'id : " + id));
    }

    // POST /products
    // Crée un nouveau produit à partir des données JSON envoyées dans la requête
    @PostMapping
    public Product create(@RequestBody Product product) {
        return repository.save(product); // Sauvegarde le produit et le retourne avec son id généré
    }

    // PUT /products/{id}
    // Met à jour un produit existant identifié par son id
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        // Cherche le produit existant ou lance une exception
        Product existing = repository.findById(id).orElseThrow(() -> 
            new RuntimeException("Produit non trouvé avec l'id : " + id));

        // Met à jour ses propriétés
        existing.setName(product.getName());
        existing.setPrice(product.getPrice());

        // Enregistre les modifications et retourne le produit mis à jour
        return repository.save(existing);
    }

    // DELETE /products/{id}
    // Supprime un produit identifié par son id
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id); // Supprime directement via JpaRepository
    }

    // POST /products/bundle
    // Crée un "bundle" à partir d'une liste d'ids de produits
    @PostMapping("/bundle")
    public Product createBundle(@RequestBody List<Long> sourceIds) {
        // Récupère tous les produits correspondant aux ids reçus
        List<Product> sources = repository.findAllById(sourceIds);

        // Vérifie que tous les produits ont été trouvés
        if (sources.size() != sourceIds.size()) {
            throw new RuntimeException("Certains produits sources n'ont pas été trouvés");
        }

        // Vérifie qu'il n'y a pas de boucle dans les sources (ex: produit A contient B, B contient A)
        if (hasRecursion(sources, sources)) {
            throw new RuntimeException("Boucle détectée dans les produits sources");
        }

        // Construit le nom du bundle en concaténant les noms des produits sources avec un " + "
        StringBuilder bundleName = new StringBuilder();
        double totalPrice = 0;

        for (Product p : sources) {
            if (bundleName.length() > 0) {
                bundleName.append(" + ");
            }
            bundleName.append(p.getName());
            totalPrice += p.getPrice();
        }

        // Crée un nouveau produit bundle avec le nom et le prix calculés, et les sources associées
        Product bundle = new Product();
        bundle.setName(bundleName.toString());
        bundle.setPrice(totalPrice);
        bundle.setSources(sources);

        // Sauvegarde le bundle en base et le retourne
        return repository.save(bundle);
    }

    // Méthode privée pour vérifier la récursivité / boucle dans les produits sources
    // Prend en paramètres les produits à vérifier et la liste originale des sources
    private boolean hasRecursion(List<Product> productsToCheck, List<Product> originalSources) {
        for (Product product : productsToCheck) {
            List<Product> sources = product.getSources();

            // Si ce produit a des sources associées
            if (sources != null && !sources.isEmpty()) {
                for (Product source : sources) {
                    // Si une source fait partie des produits originaux, on a une boucle
                    if (originalSources.contains(source)) {
                        return true;
                    }
                    // Vérifie récursivement les sources du produit source
                    if (hasRecursion(List.of(source), originalSources)) {
                        return true;
                    }
                }
            }
        }
        return false; // Pas de boucle détectée
    }
}
