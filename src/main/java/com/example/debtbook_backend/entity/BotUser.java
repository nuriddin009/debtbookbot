package com.example.debtbook_backend.entity;


import com.example.debtbook_backend.utils.BotSteps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;



@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class BotUser implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;


    private String chatId;

    private String step = BotSteps.START;

    private String selected_language;

    private String fullName;

    @Column(unique = true)
    private String phoneNumber;

    private String role;

    private String username;

    private String password;

    private String myQuery;

    @OneToOne
    private Store store;

    private Integer myOffset = 0;

    private UUID debtorId;


    private Integer startMsgId;
    private Integer langBntId;
    private Integer contactBtnId;
    private Integer locationBtnId;
    private Integer msgId;
    private Integer menuBntId;
    private Integer excelMsgId;


    @CreationTimestamp
    private Timestamp created_at;

    @UpdateTimestamp
    private Timestamp updated_at;

    @ManyToMany
    private List<Role> authorities;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public BotUser(String chatId) {
        this.chatId = chatId;
    }
}
