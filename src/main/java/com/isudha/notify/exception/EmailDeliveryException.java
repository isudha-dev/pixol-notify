package com.isudha.notify.exception;


public class EmailDeliveryException extends RuntimeException{

    public EmailDeliveryException(String message, int status){
        super(message);
    }
}
