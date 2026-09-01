package com.ramennsama.springboot.lms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();
    private int status;
    private String error;
    private String message;
    private T data;
    private String path;
}
