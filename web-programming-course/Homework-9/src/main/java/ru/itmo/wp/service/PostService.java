package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.domain.Tag;
import ru.itmo.wp.repository.PostRepository;
import ru.itmo.wp.repository.TagRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public PostService(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }

    public Long isValidId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public Set<Tag> getTags(String tags) {
        Set<Tag> set = Arrays.stream(tags.split("\\s+"))
                .map(Tag::new)
                .collect(Collectors.toSet());
        set.forEach(tag -> {
            if (!tagRepository.existsByName(tag.getName())) {
                tagRepository.save(tag);
            } else {
                tag.setId(tagRepository.findByName(tag.getName()).getId());
            }
        });
        return set;
    }

    public Post findById(Long id) {
        return id == null ? null : postRepository.findById(id).orElse(null);
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByCreationTimeDesc();
    }

    public void save(Post post) {
        postRepository.save(post);
    }
}
