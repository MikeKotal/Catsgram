package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;
import ru.yandex.practicum.catsgram.service.sorting.SortOrder;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PostService {

    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(String sort, Integer size, Long from) {
        Collection<Post> sortedPosts;
        if (SortOrder.from(sort).equals(SortOrder.DESCENDING)) {
            sortedPosts = posts.values().stream().sorted((Comparator.comparing(Post::getPostDate).reversed())).toList();
        } else {
            sortedPosts = posts.values().stream().sorted((Comparator.comparing(Post::getPostDate))).toList();
        }
        return sortedPosts.stream().skip(from).limit(size).toList();
    }

    public Post getPostById(Long id) {
        if (!posts.containsKey(id)) {
            throw new NotFoundException("Пост с id = " + id + " не найден");
        }
        return posts.get(id);
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        Optional<User> currentUser = userService.findUserById(post.getAuthorId());
        if (currentUser.isEmpty()) {
            throw new ConditionsNotMetException(String.format("Автор с id = %s не найден", post.getAuthorId()));
        }
        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
