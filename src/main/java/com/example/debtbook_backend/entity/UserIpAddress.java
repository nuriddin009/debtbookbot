package com.example.debtbook_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserIpAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String ipAddress;
    private Integer count;

    public UserIpAddress(String ipAddress, Integer count) {
        this.ipAddress = ipAddress;
        this.count = count;
    }
}
