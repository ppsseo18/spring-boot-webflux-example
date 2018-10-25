package com.example.webflux.repository;

import com.example.webflux.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, Integer> {
}
