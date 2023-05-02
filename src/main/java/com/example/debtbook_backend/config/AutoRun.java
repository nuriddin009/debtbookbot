package com.example.debtbook_backend.config;

import com.example.debtbook_backend.entity.BotUser;
import com.example.debtbook_backend.repository.RoleRepository;
import com.example.debtbook_backend.repository.UserRepository;
import com.example.debtbook_backend.utils.BotSteps;
import com.example.debtbook_backend.utils.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AutoRun implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findAll().size() == 0) {
            BotUser user = new BotUser();
            user.setPhoneNumber("+998999686653");
            user.setRole(Role.SUPER_USER);
            user.setFullName("Nuriddin Inoyatov");
            user.setChatId("1486914669");
            user.setSelected_language("uz");
            user.setStep(BotSteps.MAIN_MENU);
            user.setUsername("debtbookshiftacademy");
            user.setPassword(passwordEncoder.encode("debtbookshiftacademy"));
            com.example.debtbook_backend.entity.Role role = new com.example.debtbook_backend.entity.Role();
            role.setAuthority("ROLE_ADMIN");
            com.example.debtbook_backend.entity.Role save = roleRepository.save(role);
            List<com.example.debtbook_backend.entity.Role> roles = new ArrayList<>();
            roles.add(save);
            user.setAuthorities(roles);
            userRepository.save(user);
        }


    }
}
