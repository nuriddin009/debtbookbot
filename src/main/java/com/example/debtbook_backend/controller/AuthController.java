package com.example.debtbook_backend.controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.debtbook_backend.config.JwtTokenProvider;
import com.example.debtbook_backend.config.SecurityConfig;
import com.example.debtbook_backend.dto.JwtAuthResponse;
import com.example.debtbook_backend.entity.Role;
import com.example.debtbook_backend.payload.ReqLogin;
import com.example.debtbook_backend.service.AuthConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;

@CrossOrigin("*")
@RequestMapping("/api")
@RestController
public class AuthController {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    SecurityConfig securityConfig;

    @Autowired
    AuthConfig authConfig;

    @PostMapping("/login")
    public HttpEntity<JwtAuthResponse> loginService(@RequestBody ReqLogin reqLogin, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return ResponseEntity.ok(authConfig.signIn(reqLogin, request));
    }



    @GetMapping("/me")
    public JwtAuthResponse getMe(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        DecodedJWT decodedJWT = jwtTokenProvider.validateToken(token);
        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asString().split(",");
        List<Role> list = new ArrayList<>();
        stream(roles).forEach(role -> list.add(new Role(list.size(), role)));
        return new JwtAuthResponse(list, username);
    }

}
