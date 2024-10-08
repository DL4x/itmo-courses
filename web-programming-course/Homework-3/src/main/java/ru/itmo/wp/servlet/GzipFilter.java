package ru.itmo.wp.servlet;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpFilter;
import javax.servlet.ServletException;
import java.util.zip.GZIPOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GzipFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String acceptEncodingHeaderValue = request.getHeader("Accept-Encoding");
        if (acceptEncodingHeaderValue != null
                && acceptEncodingHeaderValue.toLowerCase().contains("gzip")) {
            DelayedHttpServletResponse delayedResponse = new DelayedHttpServletResponse(response);
            response.setHeader("Content-Encoding", "gzip");
            chain.doFilter(request, delayedResponse);
            ServletOutputStream outputStream = response.getOutputStream();
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            gzipOutputStream.write(delayedResponse.getDelayedOutputStream().toByteArray());
            gzipOutputStream.close();
        } else {
            chain.doFilter(request, response);
        }
    }
}
