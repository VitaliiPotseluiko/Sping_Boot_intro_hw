package com.springboot.intro.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Data
public class ErrorValidationDto {
    String[] errors;
    LocalDateTime timeStamp;
    HttpStatus status;
}
