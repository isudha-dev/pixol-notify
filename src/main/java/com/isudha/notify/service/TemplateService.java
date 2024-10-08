package com.isudha.notify.service;

import com.isudha.notify.dto.CreateTemplateDto;
import com.isudha.notify.exception.IncompleteTemplateDataException;
import com.isudha.notify.exception.ResourceNotFoundException;
import com.isudha.notify.model.Email;
import com.isudha.notify.model.Template;
import com.isudha.notify.model.TemplateStatus;
import com.isudha.notify.repository.EmailRepo;
import com.isudha.notify.repository.TemplateRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class TemplateService {
    private static Logger logger = LoggerFactory.getLogger(TemplateService.class);

    private TemplateRepo templateRepo;
    private EmailRepo emailRepo;

    public Template get(UUID templateId) {
        return findTemplateOrThrowError(templateId);
    }

    public Template create(CreateTemplateDto requestDto) {
        logger.info("Received request to create new template.");
        Template template = requestDto.toTemplate();
        template.setVersion(1);
        template = templateRepo.save(template);

        logger.info("Template created successfully with UUID: {}", template.getId());
        return template;
    }

    public Template update(UUID templateId, CreateTemplateDto requestDto) {
        logger.info("Received request to update template with UUID: {}", templateId);
        Template template = findTemplateOrThrowError(templateId);

        template.setVersion(template.getVersion()+1);
        template.setName(requestDto.getName());
        template.setSubject(requestDto.getSubject());
        template.setBody(requestDto.getBody());
        template = templateRepo.save(template);

        logger.info("Template updated successfully for template with UUID: {}", templateId);
        return template;

    }

    public Template changeStatus(UUID templateId, TemplateStatus status) {
        logger.info("Received request to update status for template with UUID: {}", templateId);
        Template template = findTemplateOrThrowError(templateId);

        template.setStatus(status);
        // TODO not increasing version here
        template = templateRepo.save(template);

        logger.info("Template status updated successfully for template with UUID: {}", templateId);
        return template;
    }

    public boolean delete(UUID templateId) {
        logger.info("Received request to delete template with UUID: {}", templateId);
        Template template = findTemplateOrThrowError(templateId);

        Optional<List<Email>> emailsOpt = emailRepo.findAllByTemplateId(template.getId());
        if(emailsOpt.isEmpty() || emailsOpt.get().isEmpty()) {
            logger.info("Template with ID: {} deleted successfully", templateId);
            templateRepo.delete(template);
            return true;
        }

        logger.info("Template with ID: {} not deleted because emails exists for template", templateId);
        return false;
    }

    public String updatePlaceholdersWithValue(Map<String,String> emailData, String templateString) {
        logger.info("Updating placeholders with actual value before sending email");
        Pattern pattern = Pattern.compile("\\{(\\w+)\\}");
        Matcher matcher = pattern.matcher(templateString);

        while (matcher.find()) {
            String ph = matcher.group(1);
            if(!emailData.containsKey(ph)) {
                logger.error("Actual value not received for placeholder: {}", ph);
                throw new IncompleteTemplateDataException("Missing value for placeholder: "+ph);
            }
            templateString = templateString.replace("{" + ph + "}", emailData.get(ph));
        }

        logger.info("All placeholders replaced successfully for email subject/body");
        return templateString;
    }

    private Template findTemplateOrThrowError(UUID templateId) {
        logger.info("Received get request for template with UUID: {}", templateId);

        try {
            Template template = templateRepo
                    .findById(templateId)
                    .orElseThrow(() -> new ResourceNotFoundException("Template does not exists with UUID: " + templateId));

            logger.info("Successfully retrieved template for UUID: {}", templateId);
            return template;
        } catch (ResourceNotFoundException ex) {
            logger.error("Template not found with UUID: {}", templateId);
            throw ex;
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while fetching template with UUID: {}", templateId);
            throw ex;
        }

    }
}
