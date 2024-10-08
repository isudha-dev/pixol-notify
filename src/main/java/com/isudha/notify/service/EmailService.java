package com.isudha.notify.service;

import com.isudha.notify.dto.SendEmailDto;
import com.isudha.notify.exception.EmailDeliveryException;
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

//    public EmailService(SesClient sesClient, TemplateService templateService, EmailRepo emailRepo) {
//        this.sesClient = sesClient;
//        this.templateService = templateService;
//        this.emailRepo = emailRepo;

//    }

    public Email sendEmail(SendEmailDto sendEmailDto) {
        // create pending email
        UUID templateId = UUID.fromString(sendEmailDto.getTemplateId());
        Template template = templateService.get(templateId);
        if(template.getStatus() != TemplateStatus.ACTIVE) {
            throw new RuntimeException("Template is not active.");
        }

        Email email = sendEmailDto.toEmail();
        email = emailRepo.save(email);

        // send email
        try {
            List<String> toAddresses = sendEmailDto.getTo();
            List<String> ccAddresses = sendEmailDto.getCc();

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .source(sendEmailDto.getFrom())
                    .destination(Destination.builder().toAddresses(toAddresses).ccAddresses(ccAddresses).build())
                    .message(buildMessage(templateId, sendEmailDto.getData()))
                    .build();

            SendEmailResponse emailResponse = sesClient.sendEmail(emailRequest);
            System.out.println("Message id: " + emailResponse.messageId());
            System.out.println("Sdk fields: " + emailResponse.sdkFields());

        } catch (SesException ex) {
            email.setStatus(EmailStatus.FAILED);
            emailRepo.save(email);
            throw new EmailDeliveryException(ex.getMessage(), ex.statusCode());
        }

        // update email status to success/fail
        email.setStatus(EmailStatus.SUCCESS);
        return emailRepo.save(email);
    }

    private Message buildMessage(UUID templateId, Map<String, String> data) {
        Template template = templateService.get(templateId);

        Content contentSub = Content.builder()
                .data(templateService.updatePlaceholdersWithValue(data, template.getSubject())).build();

        Content contentBody = Content.builder()
                .data(templateService.updatePlaceholdersWithValue(data, template.getBody())).build();

        return Message.builder().subject(contentSub).body(Body.builder().text(contentBody).build()).build();
    }
}
