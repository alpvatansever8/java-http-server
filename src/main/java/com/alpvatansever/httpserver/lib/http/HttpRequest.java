package com.alpvatansever.httpserver.lib.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public static HttpRequest parse(String line, BufferedReader reader) throws IOException {
        HttpRequest req = new HttpRequest();
        String[] parts = line.split(" ");
        req.method = parts[0];
        req.path = parts[1];

        String header;
        while ((header = reader.readLine()) != null && !header.isEmpty()) {
            int colon = header.indexOf(':');
            if (colon > 0) {
                req.headers.put(header.substring(0, colon).trim(), header.substring(colon + 1).trim());
            }
        }
        return req;
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getHeader(String name) { return headers.get(name); }
}
