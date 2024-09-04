package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.service.PostService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class PostPage extends Page {
    private final PostService postService;

    public PostPage(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/post/{id}")
    public String getPost(@PathVariable("id") String id, Model model) {
        model.addAttribute("post", postService.findById(postService.isValidId(id)));
        model.addAttribute("comment", new Comment());
        return "PostPage";
    }

    @PostMapping("/post/{id}")
    public String postComment(@PathVariable("id") Long id, Model model,
                              @Valid @ModelAttribute("comment") Comment comment,
                              BindingResult bindingResult,
                              HttpSession httpSession) {
        Post post = postService.findById(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "PostPage";
        }

        comment.setPost(post);
        comment.setUser(getUser(httpSession));

        post.addComment(comment);
        postService.save(post);

        model.addAttribute("post", post);

        return "redirect:/post/{id}";
    }
}
