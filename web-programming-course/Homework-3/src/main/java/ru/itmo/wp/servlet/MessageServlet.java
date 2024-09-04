package ru.itmo.wp.servlet;

import java.util.*;
import java.io.IOException;
import com.google.gson.Gson;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessageServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final Set<String> users = new HashSet<>();
    private final List<Map<String, String>> messages = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        if (uri.endsWith("/message/auth")) {
            auth(request, response);
        } else if (uri.endsWith("/message/findAll")) {
            findAll(request, response);
        } else if (uri.endsWith("/message/add")) {
            add(request, response);
        }
    }

    private void write(Object obj, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(obj));
        response.getWriter().flush();
    }

    private synchronized void auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String user = request.getParameter("user");
        if (user == null) {
            write("", response);
            return;
        }
        if (!users.contains(user)) {
            users.add(user);
            request.getSession().setAttribute("user", user);
        }
        write(user, response);
    }

    private synchronized void findAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        write(messages, response);
    }

    private synchronized void add(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String text = request.getParameter("text");
        Map<String, String> message = new HashMap<>();
        message.put("user", request.getSession().getAttribute("user").toString());
        message.put("text", text);
        messages.add(message);
        write(text, response);
    }
}
