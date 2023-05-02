package com.example.debtbook_backend.repository;

import com.example.debtbook_backend.entity.Debtor;
import com.example.debtbook_backend.projection.CustomDebtor;
import com.example.debtbook_backend.projection.DebtorCustom;
import com.example.debtbook_backend.projection.DebtorProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DebtorRepository extends JpaRepository<Debtor, UUID> {

    @Query(value = "select cast(d.id as varchar) as id, d.debtor_full_name as fullName,d.phone_number as phoneNumber\n" +
            "from debtor d\n" +
            "         inner join debtor_stores ds on d.id = ds.debtor_id\n" +
            "where d.phone_number = :phone\n" +
            "  and ds.stores_id = :storeId", nativeQuery = true)
    Optional<CustomDebtor> getIsSavedDebtor(UUID storeId, String phone);

    Optional<Debtor> findByPhoneNumber(String phoneNumber);


    @Query(value = "select cast(d.id as varchar)    as id,\n" +
            "       d.debtor_full_name              as fullName,\n" +
            "       d.phone_number                  as phoneNumber,\n" +
            "       coalesce(sum(d2.debtor_debt) - sum(d2.left_over) || ' / ' || sum(d2.debtor_debt), 0 || ' / ' || 0) as debt\n" +
            "from debtor d\n" +
            "         inner join debtor_stores ds on d.id = ds.debtor_id\n" +
            "         inner join debt d2 on ds.debtor_id = d2.debtor_id\n" +
            "where (d.phone_number ilike '%' || :search || '%' or d.debtor_full_name ilike '%' || :search || '%') " +
            "group by d.id, d.debtor_full_name, d.phone_number", nativeQuery = true)
    Page<CustomDebtor> getAllDebtors(Pageable pageable, String search);


    @Query(value = "select cast(d.id as varchar)               as id,\n" +
            "       d.debtor_full_name                         as fullName,\n" +
            "       d.phone_number                             as phoneNumber,\n" +
            "       coalesce(sum(d2.debtor_debt) - sum(d2.left_over) || ' / ' || sum(d2.debtor_debt)" +
            ", 0 || ' / ' || 0) as debt\n" +
            "from debtor d\n" +
            "         inner join debtor_stores ds on d.id = ds.debtor_id and ds.stores_id = :storeId\n" +
            "         inner join debt d2 on ds.debtor_id = d2.debtor_id\n" +
            "where (d.phone_number ilike '%' || :query || '%' or d.debtor_full_name ilike '%' || :query || '%')\n" +
            "group by d.id, d.debtor_full_name, d.phone_number", countQuery = "select cast(d.id as varchar)               as id,\n" +
            "       d.debtor_full_name                         as fullName,\n" +
            "       d.phone_number                             as phoneNumber,\n" +
            "       coalesce(sum(d2.debtor_debt) - sum(d2.left_over) || ' / ' || sum(d2.debtor_debt)" +
            ", 0 || ' / ' || 0) as debt\n" +
            "from debtor d\n" +
            "         inner join debtor_stores ds on d.id = ds.debtor_id and ds.stores_id = :storeId\n" +
            "         inner join debt d2 on ds.debtor_id = d2.debtor_id\n" +
            "where (d.phone_number ilike '%' || :query || '%' or d.debtor_full_name ilike '%' || :query || '%')\n" +
            "group by d.id, d.debtor_full_name, d.phone_number", nativeQuery = true)
    Page<CustomDebtor> getDebtors(UUID storeId, String query, Pageable pageable);

    @Query(value = "select cast(d.id as varchar) as id,\n" +
            "       d.debtor_full_name    as fullName,\n" +
            "       d.phone_number        as phoneNumber,\n" +
            "       coalesce(:debt || to_char(sum(d2.debtor_debt),'9 999 999 999 999') || chr(10) ||\n" +
            "                :paidDebt || to_char(sum(d2.debtor_debt)- sum(d2.left_over),'9 999 999 999 999') )\n" +
            "                             as debt\n" +
            "from debtor d\n" +
            "         inner join debtor_stores ds on d.id = ds.debtor_id and ds.stores_id = :storeId\n" +
            "         inner join debt d2 on ds.debtor_id = d2.debtor_id\n" +
            "where d2.active is true\n" +
            "  and (d.phone_number ilike '%' || :query || '%' or d.debtor_full_name ilike '%' || :query || '%')\n" +
            "group by d.id, d.debtor_full_name, d.phone_number", nativeQuery = true)
    List<CustomDebtor> getInlineQueryResult(
            UUID storeId,
            String query,
            String debt,
            String paidDebt
    );


    @Query(value = "select \n" +
            "       d.debtor_full_name              as fullName,\n" +
            "       d.phone_number                  as phoneNumber,\n" +
            "       coalesce(sum(d2.debtor_debt) - sum(d2.left_over) || ' / ' || sum(d2.debtor_debt)" +
            ", 0 || ' / ' || 0) as debt\n" +
            "from debtor d\n" +
            "         inner join debtor_stores ds on d.id = ds.debtor_id\n" +
            "         inner join debt d2 on ds.debtor_id = d2.debtor_id\n" +
            "group by d.id, d.debtor_full_name, d.phone_number", nativeQuery = true)
    List<DebtorProjection> getAllDebtorsList();


    @Query(value = "select d1.debtor_full_name  as fullName,\n" +
            "       d1.phone_number as phoneNumber,\n" +
            "       coalesce(sum(d.debtor_debt) - sum(d.left_over) || ' / ' || sum(d.debtor_debt)" +
            ", 0 || ' / ' || 0) as debt\n" +
            "from debtor d1\n" +
            "         inner join debtor_stores ds on d1.id = ds.debtor_id\n" +
            "         inner join debt d on ds.debtor_id = d.debtor_id\n" +
            "         inner join store s on s.id = d.store_id\n" +
            "where ds.stores_id = :storeId\n" +
            "group by d1.debtor_full_name, d1.phone_number", nativeQuery = true)
    Page<DebtorProjection> getStoreDebtor(Pageable pageable, UUID storeId);


    @Query(value = "select d.id               as id,\n" +
            "       d.debtor_full_name as fullName,\n" +
            "       d.phone_number     as phoneNumber\n" +
            "from debtor d\n" +
            "         inner join debtor_stores ds on d.id = ds.debtor_id and ds.stores_id = :storeId\n" +
            "where d.id = :debtorId\n", nativeQuery = true)
    Optional<DebtorCustom> getIsMyStoreDebtor(UUID debtorId, UUID storeId);


}
