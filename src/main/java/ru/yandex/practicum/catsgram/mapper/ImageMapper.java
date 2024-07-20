package ru.yandex.practicum.catsgram.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.dto.ImageDto;
import ru.yandex.practicum.catsgram.model.Image;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ImageMapper {
    public static ImageDto mapToImageDto(Image image) {
        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setPostId(image.getPostId());
        imageDto.setOriginalFileName(image.getOriginalFileName());
        imageDto.setFilePath(image.getFilePath());
        return imageDto;
    }
}
