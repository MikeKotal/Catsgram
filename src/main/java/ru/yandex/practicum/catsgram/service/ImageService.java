package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.dal.ImageRepository;
import ru.yandex.practicum.catsgram.dto.ImageDto;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.mapper.ImageMapper;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final PostService postService;
    private final ImageRepository imageRepository;

    @Value("${catsgram.image-directory}")
    private String imageDirectory;

    public List<ImageDto> getPostImages(Long postId) {
        return imageRepository.findAllPostImages(postId)
                .stream()
                .map(ImageMapper::mapToImageDto)
                .toList();
    }

    public List<ImageDto> saveImages(Long postId, List<MultipartFile> files) {
        return files.stream().map(file -> saveImage(postId, file)).toList();
    }

    public ImageData getImageData(Long imageId) {
        ImageDto imageDto = imageRepository.findPostImageById(imageId)
                .map(ImageMapper::mapToImageDto)
                .orElseThrow(() -> new NotFoundException("Изображение с id = " + imageId + " не найдено"));
        byte[] data = loadFile(imageDto);

        return new ImageData(data, imageDto.getOriginalFileName());
    }

    private Path saveFile(MultipartFile file, PostDto post) {
        try {
            String uniqueFileName = String.format("%d.%s", Instant.now().toEpochMilli(),
                    StringUtils.getFilenameExtension(file.getOriginalFilename()));

            Path uploadPath = Paths.get(imageDirectory, String.valueOf(post.getAuthorId()), post.getId().toString());
            Path filePath = uploadPath.resolve(uniqueFileName);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            file.transferTo(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] loadFile(ImageDto image) {
        Path path = Paths.get(image.getFilePath());
        if (Files.exists(path)) {
            try {
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new ImageFileException("Ошибка чтения файла.  Id: " + image.getId()
                        + ", name: " + image.getOriginalFileName(), e);
            }
        } else {
            throw new ImageFileException("Файл не найден. Id: " + image.getId()
                    + ", name: " + image.getOriginalFileName());
        }
    }

    private ImageDto saveImage(Long postId, MultipartFile file) {
        PostDto post = postService.getPostById(postId);
        Path filePath = saveFile(file, post);

        Image image = new Image();
        image.setFilePath(filePath.toString());
        image.setPostId(postId);
        image.setOriginalFileName(file.getOriginalFilename());

        image = imageRepository.saveImage(image);
        return ImageMapper.mapToImageDto(image);
    }
}
