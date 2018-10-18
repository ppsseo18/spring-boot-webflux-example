package com.example.webflux.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class User {
    @Id
    @Column
    private String userId;

    @Column
    private String userName;

}
