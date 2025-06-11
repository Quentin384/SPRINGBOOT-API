
# 📦 Product API – API REST de gestion de produits en Java Spring Boot

Product API est une application backend développée avec Spring Boot permettant la gestion simple de produits, avec notamment la création, mise à jour, suppression, consultation, et la création de bundles (groupes de produits combinés).  
L’API utilise une base de données H2 en mémoire pour le stockage des données.

---

## 🎯 Objectif pédagogique

Ce projet a été réalisé dans le cadre d’un TP individuel lors de ma formation, dans le but de :

- Développer une API REST complète avec Spring Boot.
- Manipuler les concepts JPA, Spring Data, et le mapping JSON.
- Implémenter des tests d’intégration avec MockMvc.
- Appliquer les bonnes pratiques REST et l’architecture MVC côté backend.

---

## 🧩 Fonctionnalités principales

### ⚙️ Gestion des produits

- Création, modification, suppression et consultation des produits via API REST.
- Chaque produit possède un nom et un prix.

### 📦 Création de bundles

- Création d’un produit bundle à partir d’une liste d’IDs produits existants.
- Le nom du bundle est la concaténation des noms des produits, et le prix est la somme de leurs prix.

### 🔍 Consultation

- Liste de tous les produits disponibles.
- Détails d’un produit spécifique.

---

## 🛠️ Technologies utilisées

| Composant       | Détail                                 |
|-----------------|---------------------------------------|
| Langage         | Java 17                               |
| Framework       | Spring Boot 3.5                       |
| Base de données | H2 (en mémoire)                       |
| Tests           | JUnit 5, MockMvc                      |
| JSON            | Jackson                              |
| Build           | Maven                                |

---

## 🗃️ Structure du code

- **model** : classes POJO représentant les entités (Product).
- **repository** : interfaces Spring Data JPA pour l’accès aux données.
- **controller** : contrôleur REST exposant les endpoints API.
- **test** : tests d’intégration avec MockMvc pour valider les endpoints.

---

## 🚀 Démarrage rapide

### 1. Cloner le projet

```
git clone <https://github.com/Quentin384/SPRINGBOOT-API>
cd product-api
```

### 2. Compiler et lancer les tests

```
mvn clean test
```

### 3. Lancer l’application

```
mvn spring-boot:run
```

L’application est accessible sur [http://localhost:8080](http://localhost:8080).

---

## 🔧 Commandes curl pour tester l’API

### Créer les produits "Clavier", "Écran", "Souris"

```
curl -X POST http://localhost:8080/products \
     -H "Content-Type: application/json" \
     -d '{"name":"Clavier","price":45.0}'

curl -X POST http://localhost:8080/products \
     -H "Content-Type: application/json" \
     -d '{"name":"Écran","price":150.0}'

curl -X POST http://localhost:8080/products \
     -H "Content-Type: application/json" \
     -d '{"name":"Souris","price":25.0}'
```

### Lister tous les produits

```
curl -X GET http://localhost:8080/products
```

### Créer un bundle avec les IDs des produits (remplacer par les vrais IDs)

```
curl -X POST http://localhost:8080/products/bundle \
     -H "Content-Type: application/json" \
     -d '[1, 2, 3]'
```

### Mettre à jour un produit (exemple pour ID=1)

```
curl -X PUT http://localhost:8080/products/1 \
     -H "Content-Type: application/json" \
     -d '{"name":"Clavier Mécanique","price":60.0}'
```

### Supprimer un produit (exemple pour ID=3)

```
curl -X DELETE http://localhost:8080/products/3
```

---

## 📌 Auteur

👨‍💻 Quentin – Étudiant développeur Fullstack Java  
🎓 Projet réalisé en solo dans le cadre de la formation Simplon  
📆 Juin 2025

---

Ce projet constitue une base solide pour évoluer vers une API plus complète :  
gestion utilisateurs, authentification, interface frontend, persistance avancée, etc.

