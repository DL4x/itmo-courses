package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.form.NoticeData;
import ru.itmo.wp.form.validator.NoticeDataValidator;
import ru.itmo.wp.service.NoticeService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

@Controller
public class NoticePage extends Page {
    private final NoticeService noticeService;
    private final NoticeDataValidator noticeDataValidator;

    public NoticePage(NoticeService noticeService, NoticeDataValidator noticeDataValidator) {
        this.noticeService = noticeService;
        this.noticeDataValidator = noticeDataValidator;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (noticeDataValidator.supports(Objects.requireNonNull(binder.getTarget()).getClass())) {
            binder.addValidators(noticeDataValidator);
        }
    }

    @GetMapping("/notice/add")
    public String noticeAddGet(Model model) {
        model.addAttribute("noticeForm", new NoticeData());
        return "NoticePage";
    }

    @PostMapping("/notice/add")
    public String noticeAddPost(@Valid @ModelAttribute("noticeForm") NoticeData noticeForm,
                                BindingResult bindingResult,
                                HttpSession httpSession) {
        if (bindingResult.hasErrors()) {
            return "NoticePage";
        }

        if (getUser(httpSession) == null) {
            return "redirect:/";
        }

        noticeService.addNotice(noticeForm);
        setMessage(httpSession, "The notice was successfully added");

        return "redirect:/";
    }

    /* @GetMapping("/notice/{id}")
    public String noticeGet(@PathVariable("id") String id) {
        TODO
    } */
}
