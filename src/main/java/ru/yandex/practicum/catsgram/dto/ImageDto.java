package ru.yandex.practicum.catsgram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ImageDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private Long postId;
    private String originalFileName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String filePath;
}
