package com.example.productapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.productapi.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {}
