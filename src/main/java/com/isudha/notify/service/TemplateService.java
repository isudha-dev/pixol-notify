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
    private TemplateRepo templateRepo;
    private EmailRepo emailRepo;

    public Template get(UUID templateId) {
        return findTemplateOrThrowError(templateId);
    }

    public Template create(CreateTemplateDto requestDto) {
        Template template = requestDto.toTemplate();
        template.setVersion(1);
        return templateRepo.save(template);
    }

    public Template update(UUID templateId, CreateTemplateDto requestDto) {
        Template template = findTemplateOrThrowError(templateId);

        template.setVersion(template.getVersion()+1);
        template.setName(requestDto.getName());
        template.setSubject(requestDto.getSubject());
        template.setBody(requestDto.getBody());
        return templateRepo.save(template);
    }

    public Template changeStatus(UUID templateId, TemplateStatus status) {
        Template template = findTemplateOrThrowError(templateId);

        template.setStatus(status);
        // TODO not increasing version here
        return templateRepo.save(template);
    }

    public boolean delete(UUID templateId) {
        Template template = findTemplateOrThrowError(templateId);

        Optional<List<Email>> emailsOpt = emailRepo.findAllByTemplateId(template.getId());
        if(emailsOpt.isEmpty() || emailsOpt.get().isEmpty()) {
            templateRepo.delete(template);
            return true;
        }
        return false;
    }

    public String updatePlaceholdersWithValue(Map<String,String> emailData, String templateString) {
        Pattern pattern = Pattern.compile("\\{(\\w+)\\}"); // "\\{(\\w+)\\}"
        Matcher matcher = pattern.matcher(templateString);

        while (matcher.find()) {
            String ph = matcher.group(1);
            if(!emailData.containsKey(ph)) {
                throw new IncompleteTemplateDataException("Missing value for placeholder: "+ph);
            }
            templateString = templateString.replace("{" + ph + "}", emailData.get(ph));
        }
        return templateString;
    }

    private Template findTemplateOrThrowError(UUID templateId) {
        return templateRepo
                .findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template with id " + templateId + " not found"));
    }
}
