package com.example.webflux.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Document
public class Product {
    @Id
    private Integer id;

    private String name;

    private Integer quantity;
}