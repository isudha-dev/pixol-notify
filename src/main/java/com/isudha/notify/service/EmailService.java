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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ses.SesClient;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private SesClient sesClient;
    private TemplateService templateService;
    private EmailRepo emailRepo;

    public Email sendEmail(SendEmailDto sendEmailDto) {

        logger.info("Received request for sending email");
        UUID templateId = UUID.fromString(sendEmailDto.getTemplateId());
        Template template = templateService.get(templateId);
        if(template.getStatus() != TemplateStatus.ACTIVE) {
            logger.error("Send email request received for not-active template");
            throw new InvalidTemplateException("Template is not active.");
        }

        Message message = buildMessage(template, sendEmailDto.getData());

        Email email = sendEmailDto.toEmail();
        email = emailRepo.save(email);
        logger.info("New email saved to db");

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
            logger.info("Email sent successfully. Message Id: {}", emailResponse.messageId());

        } catch (SesException ex) {
            email.setStatus(EmailStatus.FAILED);
            emailRepo.save(email);
            logger.error("Failed to send email, received SES exception");
            throw new EmailDeliveryException(ex.getMessage(), ex.statusCode());
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while sending email with UUID: {}", email.getId());
            throw ex;
        }

        // update email status to success/fail
        email.setStatus(EmailStatus.SUCCESS);
        return emailRepo.save(email);
    }

    public EmailStatus getEmailStatus(UUID emailId) {
        logger.info("Received get status request for email with UUID: {}", emailId);
        Email email = getEmail(emailId);

        logger.debug("Returning status for email successfully");
        return email.getStatus();
    }

    public Email getEmail(UUID emailId) {
        logger.info("Received get request for email with UUID: {}", emailId);

        try {
            Email email = emailRepo
                    .findById(emailId)
                    .orElseThrow(() -> new ResourceNotFoundException("Email does not exists with UUID: " + emailId));

            logger.info("Successfully retrieved email for UUID: {}", emailId);
            return email;
        } catch (ResourceNotFoundException ex) {
            logger.error("Email not found with UUID: {}", emailId);
            throw ex;
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while fetching email with UUID: {}", emailId);
            throw ex;
        }
    }

    private Message buildMessage(Template template, Map<String, String> data) {

        logger.info("Building message");
        Content contentSub = Content.builder()
                .data(templateService.updatePlaceholdersWithValue(data, template.getSubject())).build();

        Content contentBody = Content.builder()
                .data(templateService.updatePlaceholdersWithValue(data, template.getBody())).build();

        return Message.builder().subject(contentSub).body(Body.builder().html(contentBody).build()).build();
    }

}
