package com.alpvatansever.httpserver;

import com.alpvatansever.httpserver.filter.BasicAuthFilter;
import com.alpvatansever.httpserver.filter.LoggingFilter;
import com.alpvatansever.httpserver.lib.BaseController;
import com.alpvatansever.httpserver.lib.FrontController;
import com.alpvatansever.httpserver.lib.Server;
import com.alpvatansever.httpserver.lib.filter.Filter;
import com.alpvatansever.httpserver.lib.http.HttpRequest;
import com.alpvatansever.httpserver.lib.http.HttpResponse;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Filter chain: logging first, then authentication
        var filters = new ArrayList<Filter>();
        filters.add(new LoggingFilter());
        filters.add(new BasicAuthFilter());

        FrontController.registerFilterChain(filters);

        FrontController.addRoute("/hello", new BaseController() {
            @Override
            public void doGet(HttpRequest req, HttpResponse res) throws Exception {
                res.setStatus(200, "OK");
                res.getWriter().println("Hello, authenticated user!");
                res.flush();
            }
        });

        try (var server = new Server()) {
            server.start(8080);
        }
    }
}
