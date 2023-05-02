package com.example.debtbook_backend.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.debtbook_backend.entity.BotUser;
import com.example.debtbook_backend.repository.UserRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    UserRepository userRepository;

    public String generateAccessToken(UserDetails userPrincipal) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        BotUser botUser = userRepository.findByUsername(userPrincipal.getUsername()).orElseThrow(() -> new ServiceException("User not found"));
        String result = botUser.getAuthorities().stream()
                .map(n -> String.valueOf(n.getAuthority()))
                .collect(Collectors.joining(","));
        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withClaim("roles",
                           result
                )
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String authToken) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(authToken);
    }

//    public static DecodedJWT validateTokenHandle(String authToken) {
//        Algorithm algorithm = Algorithm.HMAC256("ShiftManagemShiftManagementDashboardSecretKeyentDashboardSecretKey".getBytes());
//        JWTVerifier verifier = JWT.require(algorithm).build();
//        return verifier.verify(authToken);
//    }


}
