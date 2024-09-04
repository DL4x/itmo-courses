package ru.itmo.wp.web.page;

import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.TalkService;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class TalksPage extends Page {
    private final UserService userService = new UserService();
    private final TalkService talkService = new TalkService();

    @Override
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        if (getUser() == null) {
            throw new RedirectException("/index");
        }
        view.put("users", userService.findAll());
        view.put("currentUser", getUser().getLogin());
        view.put("talks", talkService.findAllByUserId(getUser().getId()));
    }

    private void send(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        talkService.validateSend(request.getParameter("targetUser"));
        talkService.saveTalk(getUser().getId(),
                Long.parseLong(request.getParameter("targetUser")), request.getParameter("targetText"));
        throw new RedirectException("/index");
    }
}
