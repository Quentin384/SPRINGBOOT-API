package com.example.productapi;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.productapi.model.Product;
import com.example.productapi.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Permet de simuler les requêtes HTTP vers le contrôleur

    @Autowired
    private ProductRepository repository; // Pour manipuler les données en base pendant les tests

    @Autowired
    private ObjectMapper objectMapper; // Pour convertir les objets Java en JSON et inversement

    @BeforeEach
    void setup() {
        repository.deleteAll(); // Nettoie la base avant chaque test pour garantir l’isolation
    }

    @Test
    void testCreateAndGetProduct() throws Exception {
        Product product = new Product();
        product.setName("Clavier");
        product.setPrice(45.0);

        // Teste la création d’un produit via POST /products
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON) // Type de contenu JSON
                .content(objectMapper.writeValueAsString(product))) // Envoie le produit converti en JSON
                .andExpect(status().isCreated()) // Vérifie que la réponse HTTP est 201 Created
                .andExpect(jsonPath("$.name", is("Clavier"))) // Vérifie que le nom retourné est correct
                .andExpect(jsonPath("$.price", is(45.0))); // Vérifie que le prix retourné est correct

        // Teste la récupération de tous les produits via GET /products
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk()) // Vérifie que la réponse HTTP est 200 OK
                .andExpect(jsonPath("$", hasSize(1))); // Vérifie qu’il y a bien un produit dans la liste
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Crée un produit directement en base
        Product product = repository.save(new Product("Écran", 150.0));

        // Modifie les données du produit
        product.setName("Écran 27 pouces");
        product.setPrice(180.0);

        // Teste la mise à jour du produit via PUT /products/{id}
        mockMvc.perform(put("/products/" + product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product))) // Envoie les nouvelles données JSON
                .andExpect(status().isOk()) // Vérifie que la réponse est 200 OK
                .andExpect(jsonPath("$.name", is("Écran 27 pouces"))) // Vérifie le nom modifié
                .andExpect(jsonPath("$.price", is(180.0))); // Vérifie le prix modifié
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Crée un produit en base
        Product product = repository.save(new Product("Souris", 25.0));

        // Teste la suppression du produit via DELETE /products/{id}
        mockMvc.perform(delete("/products/" + product.getId()))
                .andExpect(status().isNoContent()); // Vérifie que la réponse est 204 No Content

        // Vérifie qu’il n’y a plus de produit en base
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testCreateBundle() throws Exception {
        // Crée deux produits en base
        Product p1 = repository.save(new Product("Souris", 25.0));
        Product p2 = repository.save(new Product("Clavier", 45.0));

        List<Long> ids = List.of(p1.getId(), p2.getId()); // Liste des ids des produits à "bundler"

        // Teste la création d’un bundle via POST /products/bundle
        mockMvc.perform(post("/products/bundle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids))) // Envoie la liste des ids en JSON
                .andExpect(status().isCreated()) // Vérifie que la réponse est 201 Created
                .andExpect(jsonPath("$.name", is("Souris + Clavier"))) // Vérifie le nom du bundle généré
                .andExpect(jsonPath("$.price", is(70.0))); // Vérifie le prix total calculé du bundle
    }

}
