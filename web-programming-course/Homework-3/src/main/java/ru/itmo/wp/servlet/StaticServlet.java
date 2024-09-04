package ru.itmo.wp.servlet;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] uri = request.getRequestURI().split("\\+");
        try (OutputStream outputStream = response.getOutputStream()) {
            for (String u : uri) {
                File file = new File("/Users/egorshulpin/Desktop/Web Programming/Homework-3/hw3/src/main/webapp/static", u);
                if (!file.isFile()) {
                    file = new File(getServletContext().getRealPath("/static" + u));
                }
                if (file.isFile()) {
                    if (response.getContentType() == null) {
                        response.setContentType(getServletContext().getMimeType(file.getName()));
                    }
                    Files.copy(file.toPath(), outputStream);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
        }
    }
}
