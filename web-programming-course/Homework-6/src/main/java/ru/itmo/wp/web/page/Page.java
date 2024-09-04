package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import com.google.common.base.Strings;
import ru.itmo.wp.model.service.UserService;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public abstract class Page {
    private HttpServletRequest request;

    private Object get(String type) {
        return request.getSession().getAttribute(type);
    }

    private void set(String type, Object obj) {
        request.getSession().setAttribute(type, obj);
    }

    protected User getUser() {
        return (User) get("user");
    }

    protected void setUser(User user) {
        set("user", user);
    }

    protected void putUser(Map<String, Object> view) {
        User user = getUser();
        if (user != null) {
            view.put("user", user);
        }
    }

    protected String getMessage() {
        return (String) get("message");
    }

    protected void setMessage(String message) {
        set("message", message);
    }

    protected void putMessage(Map<String, Object> view) {
        String message = getMessage();
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            request.getSession().removeAttribute("message");
        }
    }

    protected void before(HttpServletRequest request, Map<String, Object> view) {
        this.request = request;
        putUser(view);
        UserService service = new UserService();
        view.put("userCount", service.findAll().size());
    }

    protected void action(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    protected void after(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }
}
