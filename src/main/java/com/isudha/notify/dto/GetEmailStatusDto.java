package com.isudha.notify.dto;

import com.isudha.notify.model.EmailStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class GetEmailStatusDto {
    private UUID emailId;
    private EmailStatus status;

}
