package com.isudha.notify.service;

import com.isudha.notify.dto.SendEmailDto;
import com.isudha.notify.exception.EmailDeliveryException;
import com.isudha.notify.exception.InvalidTemplateException;
import com.isudha.notify.exception.ResourceNotFoundException;
import com.isudha.notify.model.Email;
import com.isudha.notify.model.EmailStatus;
import com.isudha.notify.model.Template;
import com.isudha.notify.model.TemplateStatus;
import com.isudha.notify.repository.EmailRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.ses.SesClient;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailService {
    private SesClient sesClient;
    private TemplateService templateService;
    private EmailRepo emailRepo;

    public Email sendEmail(SendEmailDto sendEmailDto) {

        UUID templateId = UUID.fromString(sendEmailDto.getTemplateId());
        Template template = templateService.get(templateId);
        if(template.getStatus() != TemplateStatus.ACTIVE) {
            throw new InvalidTemplateException("Template is not active.");
        }

        Message message = buildMessage(template, sendEmailDto.getData());

        Email email = sendEmailDto.toEmail();
        email = emailRepo.save(email);

        // send email
        try {
            List<String> toAddresses = sendEmailDto.getTo();
            List<String> ccAddresses = sendEmailDto.getCc();

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .source(sendEmailDto.getFrom())
                    .destination(Destination.builder().toAddresses(toAddresses).ccAddresses(ccAddresses).build())
                    .message(message)
                    .build();

            SendEmailResponse emailResponse = sesClient.sendEmail(emailRequest);

        } catch (SesException ex) {
            email.setStatus(EmailStatus.FAILED);
            emailRepo.save(email);
            throw new EmailDeliveryException(ex.getMessage(), ex.statusCode());
        }

        // update email status to success/fail
        email.setStatus(EmailStatus.SUCCESS);
        return emailRepo.save(email);
    }

    public EmailStatus getEmailStatus(UUID emailId) {
        Email email = getEmail(emailId);
        return email.getStatus();
    }

    public Email getEmail(UUID emailId) {
        return emailRepo.findById(emailId).orElseThrow(() -> new ResourceNotFoundException("Email does not exists with UUID: " +emailId));
    }

    private Message buildMessage(Template template, Map<String, String> data) {
        Content contentSub = Content.builder()
                .data(templateService.updatePlaceholdersWithValue(data, template.getSubject())).build();

        Content contentBody = Content.builder()
                .data(templateService.updatePlaceholdersWithValue(data, template.getBody())).build();

        return Message.builder().subject(contentSub).body(Body.builder().html(contentBody).build()).build();
    }

}
