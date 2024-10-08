package com.isudha.notify.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Temporal(value = TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Temporal(value =  TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date modifiedAt;
}
