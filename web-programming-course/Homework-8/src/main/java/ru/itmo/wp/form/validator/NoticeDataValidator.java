package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.NoticeData;

@Component
public class NoticeDataValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return NoticeData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            NoticeData noticeForm = (NoticeData) target;
            if (noticeForm.getContent().trim().isEmpty()) {
                errors.rejectValue("content", "content.is-empty", "Notice can not be empty");
            }
        }
    }
}
