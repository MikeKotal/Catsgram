package ru.yandex.practicum.catsgram.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.dal.mappers.PostRowMapper;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository extends BaseRepository<Post> {
    private static final String FIND_ALL_POSTS_QUERY = "SELECT * FROM posts";
    private static final String FIND_POST_BY_ID_QUERY = "SELECT * FROM posts WHERE id = ?";
    private static final String INSERT_POST_QUERY = "INSERT INTO posts(author_id, description, post_date) " +
            "VALUES(?, ?, ?) returning id";
    private static final String UPDATE_POST_QUERY = "UPDATE posts SET author_id = ?, description = ? WHERE id = ?";

    public PostRepository(JdbcTemplate jdbc, PostRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Post> findAllPosts() {
        return findMany(FIND_ALL_POSTS_QUERY);
    }

    public Optional<Post> findPostById(Long id) {
        return findOne(FIND_POST_BY_ID_QUERY, id);
    }

    public Post savePost(Post post) {
        long id = insert(
                INSERT_POST_QUERY,
                post.getAuthorId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate())
        );
        post.setId(id);
        return post;
    }

    public Post updatePost(Post post) {
        update(
                UPDATE_POST_QUERY,
                post.getAuthorId(),
                post.getDescription(),
                post.getId()
        );
        return post;
    }
}
