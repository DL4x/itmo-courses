package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.wp.service.UserService;

@Controller
public class UserPage extends Page {
    private final UserService userService;

    public UserPage(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/")
    public String empty(Model model) {
        model.addAttribute("user", null);
        return "UserPage";
    }

    @GetMapping("/user/{id}")
    public String user(@PathVariable("id") String id, Model model) {
        model.addAttribute("user", userService.findById(userService.isValidId(id)));
        return "UserPage";
    }
}
