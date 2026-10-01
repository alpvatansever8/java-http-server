package com.alpvatansever.httpserver.filter;

import com.alpvatansever.httpserver.lib.filter.Filter;
import com.alpvatansever.httpserver.lib.filter.FilterChain;
import com.alpvatansever.httpserver.lib.http.HttpRequest;
import com.alpvatansever.httpserver.lib.http.HttpResponse;
import com.alpvatansever.httpserver.lib.security.Authentication;
import com.alpvatansever.httpserver.lib.security.SecurityContext;
import com.alpvatansever.httpserver.lib.security.SecurityUtil;

public class BasicAuthFilter implements Filter {
    // Credentials come from the environment; the defaults are for local demos only
    private static final String USER = System.getenv().getOrDefault("AUTH_USER", "admin");
    private static final String PASS = System.getenv().getOrDefault("AUTH_PASS", "changeme");

    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) throws Exception {
        String auth = request.getHeader("Authorization");

        if (auth != null && auth.toLowerCase().startsWith("basic ")) {
            String creds = SecurityUtil.decodeToString(auth.substring(6).trim());
            String[] parts = creds.split(":", 2);
            if (parts.length == 2 && USER.equals(parts[0]) && PASS.equals(parts[1])) {
                SecurityContext.setAuthentication(new Authentication(parts[0]));
                chain.doFilter(request, response);
                return;
            }
        }

        System.out.println("Auth failed: " + request.getMethod() + " " + request.getPath());
        response.setStatus(401, "Unauthorized");
        response.setHeader("WWW-Authenticate", "Basic realm=\"Restricted\"");
        response.getWriter().println("401 Unauthorized - Access denied");
        response.flush();
    }
}
