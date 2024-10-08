package com.isudha.notify.controller;

import com.isudha.notify.dto.GenericSuccessResponseDto;
import com.isudha.notify.dto.GetEmailStatusDto;
import com.isudha.notify.dto.SendEmailDto;
import com.isudha.notify.model.Email;
import com.isudha.notify.model.EmailStatus;
import com.isudha.notify.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/email")
@AllArgsConstructor
public class EmailController {

    private EmailService emailService;

    // send email
    @PostMapping
    public ResponseEntity<GenericSuccessResponseDto<Email>> sendEmail(@RequestBody SendEmailDto sendEmailDto) {
        Email email = emailService.sendEmail(sendEmailDto);
        return new ResponseEntity<>(new GenericSuccessResponseDto<>(true, email), HttpStatus.OK);
    }

    // check email status
    @GetMapping("/{id}/getstatus")
    public ResponseEntity<GenericSuccessResponseDto<GetEmailStatusDto>> getStatus(@PathVariable("id") UUID id) {
        EmailStatus status = emailService.getEmailStatus(id);
        GetEmailStatusDto getEmailStatusDto = new GetEmailStatusDto(id, status);
        return new ResponseEntity<>(new GenericSuccessResponseDto<>(true, getEmailStatusDto), HttpStatus.OK);
    }

    // get email
    @GetMapping("/{id")
    public ResponseEntity<GenericSuccessResponseDto<Email>> get(@PathVariable("id") UUID id) {
        Email email = emailService.getEmail(id);
        return new ResponseEntity<>(new GenericSuccessResponseDto<>(true, email), HttpStatus.FOUND);
    }
}
