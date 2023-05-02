package com.example.debtbook_backend.projection;

import java.util.UUID;

public interface CustomDebtor {

    UUID getId();

    String getPhoneNumber();

    String getFullName();

    String getDebt();


}
