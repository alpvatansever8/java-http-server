package com.alpvatansever.httpserver.lib.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {
    private final BufferedWriter writer;
    private int statusCode = 200;
    private String statusMessage = "OK";
    private final Map<String, String> headers = new HashMap<>();
    // The body is buffered so the status line and headers can be written first
    private final StringWriter bodyBuffer = new StringWriter();
    private final PrintWriter bodyWriter = new PrintWriter(bodyBuffer);
    private boolean committed = false;

    public HttpResponse(BufferedWriter writer) {
        this.writer = writer;
    }

    public void setStatus(int code, String message) {
        this.statusCode = code;
        this.statusMessage = message;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public PrintWriter getWriter() {
        return bodyWriter;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    // Writes status line, headers and body once; later calls do nothing
    public void flush() throws IOException {
        if (committed) {
            return;
        }
        committed = true;

        bodyWriter.flush();
        String body = bodyBuffer.toString();

        writer.write("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n");
        for (Map.Entry<String, String> h : headers.entrySet()) {
            writer.write(h.getKey() + ": " + h.getValue() + "\r\n");
        }
        writer.write("Content-Length: " + body.getBytes(UTF_8).length + "\r\n");
        writer.write("Connection: close\r\n");
        writer.write("\r\n");
        writer.write(body);
        writer.flush();
    }
}
