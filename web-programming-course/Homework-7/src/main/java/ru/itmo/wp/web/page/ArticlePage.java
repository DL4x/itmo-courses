package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.annotation.Json;
import ru.itmo.wp.web.exception.RedirectException;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class ArticlePage {
    private final ArticleService articleService = new ArticleService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        if (request.getSession().getAttribute("user") == null) {
            request.getSession().setAttribute("message", "To post an article, you need to log into your account");
            request.getSession().setAttribute("messageType", "info");
            throw new RedirectException("/index");
        }
    }

    @Json
    private void post(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        Article article = new Article();
        User user = (User) request.getSession().getAttribute("user");
        article.setUserId(user.getId());
        article.setTitle(request.getParameter("title").trim());
        article.setText(request.getParameter("text").trim());

        articleService.validateArticle(article);
        articleService.postArticle(article);

        request.getSession().setAttribute("message", "The article was successfully published");
        request.getSession().setAttribute("messageType", "success");

        throw new RedirectException("/index");
    }
}
