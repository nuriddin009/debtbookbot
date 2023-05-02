package com.example.debtbook_backend.service;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleComponent {


    @Scheduled(cron = "0 0 9 * * * ")
    public void sendMessageToDebtors() {
        System.out.println("salom");
    }

}
