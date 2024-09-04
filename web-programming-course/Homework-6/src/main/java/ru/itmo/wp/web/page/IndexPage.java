package ru.itmo.wp.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused"})
public class IndexPage extends Page {
    @Override
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        putMessage(view);
    }
}
