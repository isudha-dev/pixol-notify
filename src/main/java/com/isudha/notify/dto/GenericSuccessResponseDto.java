package com.isudha.notify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenericSuccessResponseDto<T> {
    private boolean success;
    private T data;
}
