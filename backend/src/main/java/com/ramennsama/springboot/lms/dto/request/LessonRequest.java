package com.ramennsama.springboot.lms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonRequest {

    @NotBlank(message = "Lesson title is required")
    private String title;

    private String videoUrl;

    private String content;

    private boolean isFreePreview = false;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;
}
