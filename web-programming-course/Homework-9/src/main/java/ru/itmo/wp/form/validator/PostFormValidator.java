package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.PostForm;

import java.util.Arrays;

@Component
public class PostFormValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PostForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            PostForm postForm = (PostForm) target;
            if (!Arrays.stream(postForm.getTags().split("\\s+"))
                    .allMatch(tag -> tag.matches("[a-zA-Z]+"))) {
                errors.rejectValue("tags", "tags.invalid-tag", "Only Latin letters are allowed");
            }
        }
    }
}
