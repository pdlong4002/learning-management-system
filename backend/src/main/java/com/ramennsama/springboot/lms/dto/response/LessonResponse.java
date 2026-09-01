package com.ramennsama.springboot.lms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private Long id;
    private String title;
    private String videoUrl;
    private String content;
    private boolean isFreePreview;
    private Integer orderIndex;
}
