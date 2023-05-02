package com.example.debtbook_backend.repository;

import com.example.debtbook_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {


    @Query(value = "select coalesce(sum(p.amount),0)\n" +
            "from debt d\n" +
            "         inner join payment p on d.id = p.debt_id\n" +
            "where d.active is true\n" +
            "  and d.debtor_id=:debtorId", nativeQuery = true)
    Long debtPayments(UUID debtorId);

}
