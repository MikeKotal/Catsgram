package ru.yandex.practicum.catsgram.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.dal.PostRepository;
import ru.yandex.practicum.catsgram.dto.NewPostRequest;
import ru.yandex.practicum.catsgram.dto.PostDto;
import ru.yandex.practicum.catsgram.dto.UpdatePostRequest;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.mapper.PostMapper;
import ru.yandex.practicum.catsgram.model.Post;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final UserService userService;
    private final PostRepository postRepository;

    public List<PostDto> getPosts(String sort, Integer size, Long from) {
        if (!(sort.equals("desc") || sort.equals("asc"))) {
            throw new ParameterNotValidException("sort", "Некорректный параметр сортировки, доступны: asc, desc");
        }
        if (size <= 0) {
            throw new ParameterNotValidException("size", "Некорректный размер выборки. Размер должен быть больше нуля");
        }
        if (from < 0) {
            throw new ParameterNotValidException("from",
                    "Некорректный размер выборки. Параметр отсчета не может быть меньше нуля");
        }
        return postRepository.findAllPosts()
                .stream()
                .map(PostMapper::mapToPostDto)
                .sorted(sort.equals("desc") ? Comparator.comparing(PostDto::getPostDate).reversed()
                        : Comparator.comparing(PostDto::getPostDate))
                .skip(from)
                .limit(size)
                .toList();
    }

    public PostDto getPostById(Long id) {
        return postRepository.findPostById(id)
                .map(PostMapper::mapToPostDto)
                .orElseThrow(() -> new NotFoundException("Пост с id = " + id + " не найден"));
    }

    public PostDto createPost(NewPostRequest request) {
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        userService.getUserById(request.getAuthorId());
        Post post = PostMapper.mapToPost(request);
        post = postRepository.savePost(post);
        return PostMapper.mapToPostDto(post);
    }

    public PostDto updatePost(Long id, UpdatePostRequest request) {
        Post updatePost = postRepository.findPostById(id)
                .map(post -> PostMapper.updatePostFields(post, request))
                .orElseThrow(() -> new NotFoundException("Пост с id = " + id + " не найден"));
        updatePost = postRepository.updatePost(updatePost);
        return PostMapper.mapToPostDto(updatePost);
    }
}
