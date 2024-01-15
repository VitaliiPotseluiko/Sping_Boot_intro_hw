package com.springboot.intro.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class StatusErrorDto {
    private String error;
    private LocalDateTime timeStamp;
    private HttpStatus status;
}
