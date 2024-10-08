package com.isudha.notify.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "templates")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Template extends BaseModel{
    private int version;
    private String name;
    private String subject;
    private String body;
    private TemplateStatus status;

}
