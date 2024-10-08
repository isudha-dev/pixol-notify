package com.isudha.notify.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isudha.notify.exception.TemplateDataParsingException;
import com.isudha.notify.model.Email;
import com.isudha.notify.model.EmailStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class SendEmailDto {

    private Logger logger = LoggerFactory.getLogger(SendEmailDto.class);

    private List<String> to;
    private List<String> cc;
    private String from;
    private String templateId;
    private Map<String, String> data;

    public Email toEmail() {
        ObjectMapper mapper = new ObjectMapper();
        String templateData;

        try {
            templateData = mapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            logger.error("JSON processing error while parsing template data. Error: ", ex.getMessage());
            throw new TemplateDataParsingException("Unable to parse template data");
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while parsing template data. Error: "+ ex.getMessage());
            throw ex;
        }

        return Email.builder()
                .toAddress(String.join(",", this.to))
                .ccAddress(String.join(",", this.cc))
                .fromAddress(this.from)
                .templateId(UUID.fromString(this.templateId))
                .data(templateData)
                .status(EmailStatus.QUEUED)
                .build();
    }
}
