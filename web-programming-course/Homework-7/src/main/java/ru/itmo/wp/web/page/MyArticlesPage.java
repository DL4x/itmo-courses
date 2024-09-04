package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class MyArticlesPage {
    private final ArticleService articleService = new ArticleService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        if (request.getSession().getAttribute("user") == null) {
            request.getSession().setAttribute("message", "To edit your articles, you need to log into your account");
            request.getSession().setAttribute("messageType", "info");
            throw new RedirectException("/index");
        }
        User user = (User) request.getSession().getAttribute("user");
        view.put("articles", articleService.findAllByUserId(user.getId()));
    }

    @Json
    private void showOrHide(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        long id = Long.parseLong(request.getParameter("id"));
        long userId = articleService.find(id).getUserId();
        User user = (User) request.getSession().getAttribute("user");
        if (userId != user.getId()) {
            throw new ValidationException("It is forbidden to edit other articles");
        }
        boolean hidden = request.getParameter("hidden").equals("true");
        articleService.updateHidden(id, !hidden);
    }
}
