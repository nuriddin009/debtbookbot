package com.example.debtbook_backend.service;

import com.example.debtbook_backend.config.JwtTokenProvider;
import com.example.debtbook_backend.config.SecurityConfig;
import com.example.debtbook_backend.dto.JwtAuthResponse;
import com.example.debtbook_backend.entity.BotUser;
import com.example.debtbook_backend.entity.UserIpAddress;
import com.example.debtbook_backend.payload.ReqLogin;
import com.example.debtbook_backend.repository.UserIpAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class AuthConfig {


    @Autowired
    UserIpAddressRepository userIpAddressRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    SecurityConfig securityConfig;

    public JwtAuthResponse signIn(ReqLogin reqLogin, HttpServletRequest request){

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        Optional<UserIpAddress> byIpAddress = userIpAddressRepository.findByIpAddress(ipAddress);
        if(byIpAddress.isPresent()){
            UserIpAddress userIpAddress = byIpAddress.get();
            if(userIpAddress.getCount()>=5){
                return new JwtAuthResponse(false, "Your account is blocked");
            }
        }

        try {
            Authentication authentication = securityConfig.authenticationManagerBean().authenticate(new UsernamePasswordAuthenticationToken(reqLogin.getUsername(), reqLogin.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
            BotUser user = (BotUser) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal);
            System.out.println(accessToken);
            if(byIpAddress.isPresent()){
                byIpAddress.get().setCount(0);
                userIpAddressRepository.save(byIpAddress.get());
            }

            return new JwtAuthResponse(true, accessToken, user.getAuthorities(), user.getUsername());

        } catch (Exception exception) {
            if (byIpAddress.isPresent()){
                byIpAddress.get().setCount(byIpAddress.get().getCount()+1);
                userIpAddressRepository.save(byIpAddress.get());
            }else {
                userIpAddressRepository.save(new UserIpAddress(ipAddress,0));
            }
            return new JwtAuthResponse(false, "Login yoki parol xato!");
        }

    }


}
