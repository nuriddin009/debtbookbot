package com.example.debtbook_backend.repository;

import com.example.debtbook_backend.entity.Debt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DebtRepository extends JpaRepository<Debt, UUID> {


    @Query(value = "select *\n" +
            "from debt\n" +
            "where debtor_id = :debtorId\n" +
            "  and active is true\n" +
            "order by created_at", nativeQuery = true)
    List<Debt> getDebtorDebts(UUID debtorId);


    @Query(value = "select coalesce(:debt || to_char(sum(d.debtor_debt) + :addDebt, '9 999 999 999 999') || chr(10) ||\n" +
            "                :paidDebt ||\n" +
            "                to_char(sum(d.debtor_debt) - sum(d.left_over) + :addPayment, '9 999 999 999 999') || chr(10) ||\n" +
            "                :remainDebt || to_char(sum(d.left_over) - :addPayment, '9 999 999 999 999'),\n" +
            "                :debt || 0 + :addDebt || chr(10) || :paidDebt || 0 + :addPayment || chr(10) || :remainDebt || 0 - :addPayment)\n" +
            "           as debt\n" +
            "from debt d\n" +
            "         inner join debtor d2 on d2.id = d.debtor_id\n" +
            "         inner join store s on s.id = d.store_id\n" +
            "where d.store_id = :storeId\n" +
            "  and d2.id = :debtorId\n" +
            "  and d.active is true", nativeQuery = true)
    String getOneDebtorDebt(
            UUID storeId,
            UUID debtorId,
            Long addDebt,
            Long addPayment,
            String paidDebt,
            String debt,
            String remainDebt
    );

    @Query(value = "select coalesce(sum(d.debtor_debt),0) as debt\n" +
            "            from debt d\n" +
            "                     inner join debtor d2 on d2.id = d.debtor_id\n" +
            "                     inner join store s on s.id = d.store_id\n" +
            "            where d.store_id = :storeId\n" +
            "              and d2.id = :debtorId\n" +
            "              and d.active is true", nativeQuery = true)
    Long getOneDebtorsDebt(UUID storeId, UUID debtorId);

}
