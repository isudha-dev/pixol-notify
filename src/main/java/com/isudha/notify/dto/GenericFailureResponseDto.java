package com.isudha.notify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericFailureResponseDto {
    private boolean success;
    @JsonProperty("error_message")
    private String errorMessage;
}
