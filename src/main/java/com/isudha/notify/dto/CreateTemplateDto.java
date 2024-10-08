package com.isudha.notify.dto;

import com.isudha.notify.model.Template;
import com.isudha.notify.model.TemplateStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateTemplateDto {
    private String name;
    private String subject;
    private String body;

    public Template toTemplate() {
        return Template.builder()
                .name(name)
                .subject(subject)
                .body(body)
                .status(TemplateStatus.NEW)
                .build();
    }
}
