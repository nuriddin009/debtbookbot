package com.example.debtbook_backend.entity;


import com.example.debtbook_backend.utils.BotSteps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DebtUser {



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

    private String phoneNumber;

    @OneToMany
    private List<Debtor> debtors = new ArrayList<>();


    private Integer langBntId;
    private Integer contactBtnId;
    private Integer msgId;
    private Integer backMsgId;
    private Integer debtMsgId;


}
