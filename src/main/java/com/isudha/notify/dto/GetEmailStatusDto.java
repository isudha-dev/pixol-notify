package com.isudha.notify.dto;

import com.isudha.notify.model.EmailStatus;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class GetEmailStatusDto {
    private UUID emailId;
    private EmailStatus status;

}
