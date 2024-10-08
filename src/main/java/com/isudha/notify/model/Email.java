package com.isudha.notify.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "emails")
public class Email extends BaseModel{
    private String toAddress;
    private String ccAddress;
    private String fromAddress;
    private UUID templateId;
    private String data;
    private EmailStatus status;

}
