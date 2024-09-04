package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.annotation.Json;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class UsersPage {
    private final UserService userService = new UserService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        User user = (User) request.getSession().getAttribute("user");
        User currentUser = null;
        if (user != null) {
            currentUser = userService.find(user.getId());
            if (user.getAdmin() != currentUser.getAdmin()) {
                request.getSession().setAttribute("user", currentUser);
            }
        }
        view.put("admin", currentUser != null && currentUser.getAdmin());
    }

    @Json
    private void findAll(HttpServletRequest request, Map<String, Object> view) {
        view.put("users", userService.findAll());
    }

    @Json
    private void findUser(HttpServletRequest request, Map<String, Object> view) {
        view.put("user",
                userService.find(Long.parseLong(request.getParameter("userId"))));
    }

    private void enableOrDisable(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User user = (User) request.getSession().getAttribute("user");
        User currentUser = userService.find(user.getId());
        if (!currentUser.getAdmin()) {
            throw new ValidationException("No access rights");
        }
        long userId = Long.parseLong(request.getParameter("userId"));
        boolean disable = request.getParameter("admin").equals("true");
        userService.updateAdmin(userId, !disable);
    }
}
