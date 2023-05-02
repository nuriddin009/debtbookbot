package com.example.debtbook_backend.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.stream;

@Component
public class JwtAuthenicationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
        String token = request.getHeader("Authorization");
            if (token != null) {
                DecodedJWT decodedJWT = tokenProvider.validateToken(token);
                String username = decodedJWT.getSubject();
                String[] roles = decodedJWT.getClaim("roles").asString().split(",");
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

//            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            Claims claims = Jwts.parser()
//                    .setSigningKey(secretKey)
//                    .parseClaimsJws(token)
//                    .getBody();
//            String username = claims.getSubject();
//            String str = (String) claims.get("roles");
//            String[] roles = str.substring(0, str.length() - 1).split(",");
//            List<Role> roleList = new ArrayList<>();
//            for (String role : roles) {
//                roleList.add(new Role(null, role));
//            }
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                    username,
//                    null,
//                    roleList
//            );
//            authenticationToken.setDetails(authenticationToken);
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {

        }
            filterChain.doFilter(request, response);

    }
}