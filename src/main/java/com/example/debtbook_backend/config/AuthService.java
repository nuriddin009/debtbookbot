package com.example.debtbook_backend.config;


import com.example.debtbook_backend.entity.BotUser;
import com.example.debtbook_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<BotUser> byUsername = userRepository.findByUsername(username);
        if (byUsername.isPresent()){
            return byUsername.get();
        }else {
            throw new UsernameNotFoundException(username+"topilmadi");
        }
    }
}
