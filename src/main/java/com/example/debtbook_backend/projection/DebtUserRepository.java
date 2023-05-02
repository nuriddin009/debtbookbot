package com.example.debtbook_backend.projection;

import com.example.debtbook_backend.entity.DebtUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface DebtUserRepository extends JpaRepository<DebtUser, UUID> {

    Optional<DebtUser> findByChatId(String chatId);

}
