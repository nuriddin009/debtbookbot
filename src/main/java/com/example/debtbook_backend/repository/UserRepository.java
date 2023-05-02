package com.example.debtbook_backend.repository;

import com.example.debtbook_backend.entity.BotUser;
import com.example.debtbook_backend.projection.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<BotUser, UUID> {

    Optional<BotUser> findByChatId(String chatId);

//api
    @Query(value = "select cast(u.id as varchar)   as id,\n" +
            "       u.full_name      as fullName,\n" +
            "       s.store_name      as storeName,\n" +
            "       u.phone_number    as phoneNumber,\n" +
            "        coalesce(sum(d.debtor_debt) - sum(d.left_over) || ' / ' || sum(d.debtor_debt), 0 || ' / ' || 0) as debt,\n" +
            "       'https://www.google.com/maps/place/' || s.latitude || ',' || s.longitude || '/@' || s.latitude || ',' ||\n" +
            "       s.longitude || ',12z/data=!3m1!1e3'   as storeLocation\n" +
            "\n" +
            "from users u\n" +
            "         inner join store s on s.id = u.store_id\n" +
            "         inner join debt d on s.id = d.store_id\n" +
            "    and (u.phone_number ilike '%' || :search || '%'\n" +
            "   or u.full_name ilike '%' || :search || '%'\n" +
            "   or s.store_name ilike '%' || :search || '%')\n" +
            "group by u.id, u.full_name, u.phone_number, s.store_name, s.latitude, s.longitude", nativeQuery = true)
    Page<UserProjection> getAllUsersStore(Pageable pageable, String search);


    @Query(value = "select \n" +
            "       u.full_name      as fullName,\n" +
            "       s.store_name      as storeName,\n" +
            "       u.phone_number    as phoneNumber,\n" +
            "        coalesce(sum(d.debtor_debt) - sum(d.left_over) || ' / ' || sum(d.debtor_debt), 0 || ' / ' || 0) as debt,\n" +
            "       'https://www.google.com/maps/place/' || s.latitude || ',' || s.longitude || '/@' || s.latitude || ',' ||\n" +
            "       s.longitude || ',12z/data=!3m1!1e3'   as storeLocation\n" +
            "\n" +
            "from users u\n" +
            "         inner join store s on s.id = u.store_id\n" +
            "         inner join debt d on s.id = d.store_id\n" +
            "where d.active is true\n" +
            "group by u.id, u.full_name, u.phone_number, s.store_name, s.latitude, s.longitude", nativeQuery = true)
    List<UserProjection> getAllStores();

    @Query(value = "select * from users u where u.chat_id !='1486914669'", nativeQuery = true)
    List<BotUser> getAllU();


    Optional<BotUser> findByUsername(String username);
}
