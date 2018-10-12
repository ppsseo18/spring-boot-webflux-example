package com.example.webflux.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
