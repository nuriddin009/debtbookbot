package com.example.debtbook_backend.repository;

import com.example.debtbook_backend.entity.Store;
import com.example.debtbook_backend.projection.DebtorProjection;
import com.example.debtbook_backend.projection.ExcelDebtor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    @Query(value = "select cast(d.id as varchar)                             as id,\n" +
            "       d.phone_number                                    as phoneNumber,\n" +
            "       d.debtor_full_name                                as fullName,\n" +
            "       coalesce(:debt || to_char(sum(d2.debtor_debt), '9 999 999 999 999') || chr(10) ||\n" +
            "                :paidDebt || to_char(sum(d2.debtor_debt) - sum(d2.left_over), '9 999 999 999 999') || chr(10) ||\n" +
            "                :remainDebt || to_char(sum(d2.left_over), '9 999 999 999 999'),\n" +
            "                :debt || 0 || chr(10) || :paidDebt || 0 || chr(10) || :remainDebt || 0) as debt\n" +
            "from debtor d\n" +
            "         inner join debtor_stores ds on d.id = ds.debtor_id\n" +
            "         inner join debt d2 on d.id = d2.debtor_id\n" +
            "where ds.stores_id = :storeId\n" +
            "  and d2.active is true\n" +
            "group by d.debtor_full_name, d.phone_number, d.id\n" +
            "order by sum(d2.debtor_debt) desc",
            countQuery = "select cast(d.id as varchar)                             as id,\n" +
                    "       d.phone_number                                    as phoneNumber,\n" +
                    "       d.debtor_full_name                                as fullName,\n" +
                    "       coalesce(:debt || to_char(sum(d2.debtor_debt), '9 999 999 999 999') || chr(10) ||\n" +
                    "                :paidDebt || to_char(sum(d2.debtor_debt) - sum(d2.left_over), '9 999 999 999 999') || chr(10) ||\n" +
                    "                :remainDebt || to_char(sum(d2.left_over), '9 999 999 999 999'),\n" +
                    "                :debt || 0 || chr(10) || :paidDebt || 0 || chr(10) || :remainDebt || 0) as debt\n" +
                    "from debtor d\n" +
                    "         inner join debtor_stores ds on d.id = ds.debtor_id\n" +
                    "         inner join debt d2 on d.id = d2.debtor_id\n" +
                    "where ds.stores_id = :storeId\n" +
                    "  and d2.active is true\n" +
                    "group by d.debtor_full_name, d.phone_number, d.id\n" +
                    "order by sum(d2.debtor_debt) desc",
            nativeQuery = true)
    Page<DebtorProjection> getStoreDebtors(UUID storeId, Pageable pageable, String paidDebt, String debt,String remainDebt);


    @Query(value = "select d.phone_number   as phoneNumber,\n" +
            "                               d.debtor_full_name      as fullName,\n" +
            "                               (case\n" +
            "                                    when sum(d2.debtor_debt) is null then 0 || ' / ' || 0\n" +
            "                                    else sum(d2.debtor_debt) - sum(d2.left_over) || ' / ' || sum(d2.debtor_debt) end) as debt\n" +
            "                        from debtor d\n" +
            "                                 inner join debtor_stores ds on d.id = ds.debtor_id\n" +
            "                                 inner join debt d2 on d.id = d2.debtor_id\n" +
            "                        where ds.stores_id = :storeId\n" +
            "                          and d2.active is true\n" +
            "                        group by d.debtor_full_name, d.phone_number order by sum(d2.debtor_debt) desc", nativeQuery = true)
    List<DebtorProjection> getStoreAllDebtors(UUID storeId);


    @Query(value = "select d.phone_number                                       as phoneNumber,\n" +
            "       d.debtor_full_name                                   as fullName,\n" +
            "       coalesce(sum(d2.debtor_debt), 0)                     as debt,\n" +
            "       coalesce(sum(d2.debtor_debt) - sum(d2.left_over), 0) as paidDebt,\n" +
            "       coalesce(sum(d2.left_over))                          as remainDebt\n" +
            "from debtor d\n" +
            "         inner join debtor_stores ds on d.id = ds.debtor_id\n" +
            "         inner join debt d2 on d.id = d2.debtor_id\n" +
            "where ds.stores_id = :storeId\n" +
            "  and d2.active is true\n" +
            "group by d.debtor_full_name, d.phone_number, d2.debtor_debt, d2.left_over\n" +
            "order by sum(d2.debtor_debt) desc", nativeQuery = true)
    List<ExcelDebtor> getStoreAllDebtorsForCreateExcelFile(UUID storeId);


    @Query(value = "select coalesce(:debt || to_char(sum(d.debtor_debt), '9 999 999 999 999') || chr(10) ||\n" +
            "                :paidDebt || to_char(sum(d.debtor_debt) - sum(d.left_over), '9 999 999 999 999'),\n" +
            "                 :debt || chr(10) || :paidDebt || 0)\n" +
            "from debt d\n" +
            "where current_date - 1 < d.created_at\n" +
            "  and current_date + 1 > d.created_at\n" +
            "  and d.store_id = :storeId", nativeQuery = true)
    String getTodayReportDebt(UUID storeId, String paidDebt, String debt);



    @Query(value = "select coalesce(:debt || to_char(sum(d.debtor_debt), '9 999 999 999 999') || chr(10) ||\n" +
            "                :paidDebt || to_char(sum(d.debtor_debt) - sum(d.left_over), '9 999 999 999 999'),\n" +
            "                :debt || 0 || chr(10) || :paidDebt || 0)\n" +
            "from debt d\n" +
            "where current_date - 2 < d.created_at\n" +
            "  and current_date > d.created_at\n" +
            "  and d.store_id = :storeId", nativeQuery = true)
    String getYesterdayReportDebt(UUID storeId, String paidDebt, String debt);


    @Query(value = "select coalesce(:debt || to_char(sum(d.debtor_debt), '9 999 999 999 999') || chr(10) ||\n" +
            "                :paidDebt || to_char(sum(d.debtor_debt) - sum(d.left_over), '9 999 999 999 999'),\n" +
            "                :paidDebt || 0 || chr(10) || :debt || 0)\n" +
            "from debt d\n" +
            "where d.created_at >= current_date - interval '1 week'\n" +
            "  and d.store_id = :storeId", nativeQuery = true)
    String getWeeklyReportDebt(UUID storeId, String paidDebt, String debt);

    @Query(value = "select coalesce(:debt || to_char(sum(d.debtor_debt), '9 999 999 999 999') || chr(10) ||\n" +
            "                :paidDebt || to_char(sum(d.debtor_debt) - sum(d.left_over), '9 999 999 999 999'), :paidDebt || 0 || chr(10) || :debt || 0)\n" +
            "            from debt d\n" +
            "            where d.created_at >= current_date - interval '1 month'\n" +
            "              and d.store_id = :storeId", nativeQuery = true)
    String getMonthlyReportDebt(UUID storeId, String paidDebt, String debt);


}
