package com.alpvatansever.httpserver.lib;

import com.alpvatansever.httpserver.lib.filter.Filter;
import com.alpvatansever.httpserver.lib.filter.FilterChain;
import com.alpvatansever.httpserver.lib.http.HttpRequest;
import com.alpvatansever.httpserver.lib.http.HttpResponse;
import com.alpvatansever.httpserver.lib.security.SecurityContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FrontController {
    private static final Map<String, BaseController> ROUTES = new ConcurrentHashMap<>();
    private static final List<Filter> FILTERS = new ArrayList<>();

    private FrontController() {
    }

    public static void handle(final HttpRequest request, final HttpResponse response) {
        final var filterChain = new FilterChain(FILTERS);

        if (!FILTERS.isEmpty()) {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception exception) {
                response.setStatus(500, "Internal Server Error");
                response.getWriter().println("Server error during filtering: " + exception.getMessage());
                return;
            }
        }

        if (!filterChain.isFullyProcessed()) {
            return;
        }

        BaseController controller = ROUTES.get(request.getPath());

        if (controller == null) {
            response.setStatus(404, "Not Found");
            response.getWriter().println("404 Not Found: No route for " + request.getPath());
            return;
        }

        try {
            controller.handle(request, response);
        } catch (UnsupportedOperationException e) {
            response.setStatus(405, "Method Not Allowed");
            response.getWriter().println(e.getMessage());
        } catch (Exception exception) {
            response.setStatus(500, "Internal Server Error");
            response.getWriter().println("Server error: " + exception.getMessage());
        } finally {
            SecurityContext.clear();
        }
    }

    public static void addRoute(String path, BaseController controller) {
        ROUTES.put(path, controller);
    }

    public static void registerFilterChain(List<Filter> filters) {
        FILTERS.clear();
        FILTERS.addAll(filters);
    }
}