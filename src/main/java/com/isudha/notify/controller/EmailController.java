package com.isudha.notify.controller;

import com.isudha.notify.dto.GenericSuccessResponseDto;
import com.isudha.notify.dto.SendEmailDto;
import com.isudha.notify.model.Email;
import com.isudha.notify.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
