package com.example.debtbook_backend.repository;

import com.example.debtbook_backend.entity.UserIpAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserIpAddressRepository extends JpaRepository<UserIpAddress, Integer> {

    Optional<UserIpAddress> findByIpAddress(String ipAddress);

}
