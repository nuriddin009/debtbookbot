package com.example.debtbook_backend.projection;

import java.util.UUID;

public interface UserProjection {

    UUID getId();

    String getFullName();

    String getStoreName();

    String getPhoneNumber();

    String getDebt();

    String getStoreLocation();
}
