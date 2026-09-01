package com.ramennsama.springboot.lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SectionRequest {

    @NotBlank(message = "Section title is required")
    private String title;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;
}
