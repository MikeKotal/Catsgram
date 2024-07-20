package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.ImageRowMapper;
import ru.yandex.practicum.catsgram.model.Image;

import java.util.List;
import java.util.Optional;

@Repository
public class ImageRepository extends BaseRepository<Image> {
    private static final String FIND_ALL_POST_IMAGES_QUERY = "SELECT img.id AS id, img.original_name AS " +
            "original_name, img.file_path AS file_path, img.post_id AS post_id FROM image_storage AS img " +
            "INNER JOIN posts AS pst ON img.post_id=pst.id WHERE pst.id = ?";
    private static final String FIND_POST_IMAGE_BY_ID_QUERY = "SELECT * FROM image_storage WHERE id = ?";
    private static final String INSERT_POST_IMAGE_QUERY = "INSERT INTO image_storage(original_name, file_path, " +
            "post_id) VALUES(?, ?, ?) returning id";

    public ImageRepository(JdbcTemplate jdbc, ImageRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Image> findAllPostImages(Long id) {
        return findMany(FIND_ALL_POST_IMAGES_QUERY, id);
    }

    public Optional<Image> findPostImageById(Long id) {
        return findOne(FIND_POST_IMAGE_BY_ID_QUERY, id);
    }

    public Image saveImage(Image image) {
        long id = insert(
                INSERT_POST_IMAGE_QUERY,
                image.getOriginalFileName(),
                image.getFilePath(),
                image.getPostId()
        );
        image.setId(id);
        return image;
    }
}