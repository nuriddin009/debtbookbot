package com.example.debtbook_backend.projection;

import lombok.Data;

import java.util.UUID;

@Data
public class StoreMessageProjection {

    UUID attachmentId;

    String description;

}
