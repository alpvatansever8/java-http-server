package com.alpvatansever.httpserver.filter;

import com.alpvatansever.httpserver.lib.filter.Filter;
import com.alpvatansever.httpserver.lib.filter.FilterChain;
import com.alpvatansever.httpserver.lib.http.HttpRequest;
import com.alpvatansever.httpserver.lib.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingFilter implements Filter {
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        String time = LocalDateTime.now().format(DTF);
        System.out.println("[" + time + "] " + request.getMethod() + " " + request.getPath());

        chain.doFilter(request, response);

        System.out.println("[" + time + "] " + request.getMethod() + " " + request.getPath() +
                " -> " + response.getStatusCode() + " " + response.getStatusMessage());
    }
}
