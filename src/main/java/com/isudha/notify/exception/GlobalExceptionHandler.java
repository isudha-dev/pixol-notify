package com.isudha.notify.exception;

import com.isudha.notify.dto.GenericFailureResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericFailureResponseDto> handleResourceNotFoundEx(ResourceNotFoundException ex){
        return createResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncompleteTemplateDataException.class)
    public ResponseEntity<GenericFailureResponseDto> handleMissingPlaceholderValueEx(IncompleteTemplateDataException ex) {
        return createResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<GenericFailureResponseDto> handleEmailDeliveryException(EmailDeliveryException ex) {
        return createResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TemplateDataParsingException.class)
    public ResponseEntity<GenericFailureResponseDto> handleTemplateDataParsingException(TemplateDataParsingException ex) {
        return createResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTemplateException.class)
    public ResponseEntity<GenericFailureResponseDto> handleInvalidTemplateException(InvalidTemplateException ex) {
        return createResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericFailureResponseDto> handleException(Exception ex) {
        return createResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<GenericFailureResponseDto> createResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(new GenericFailureResponseDto(false, message), status);
    }
}
