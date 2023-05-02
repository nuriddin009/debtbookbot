package com.example.debtbook_backend.payload;


import lombok.Data;

@Data
public class ReqLogin {

    private String username;

    private String password;

}
