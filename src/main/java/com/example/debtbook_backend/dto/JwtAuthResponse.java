package com.example.debtbook_backend.dto;

import com.example.debtbook_backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

    private String token;
    private List<Role> roles;
    private String tokenType = "Bearer";
    private String username;
    private Boolean success;


    public JwtAuthResponse(Boolean success,String token, List<Role> roles, String username) {
        this.success = success;
        this.token = token;
        this.roles = roles;
        this.username = username;
    }

    public JwtAuthResponse(List<Role> roles, String username) {
        this.roles = roles;
        this.username = username;
    }

    public JwtAuthResponse(Boolean success,String username) {
        this.success = success;
        this.username = username;
    }

}
