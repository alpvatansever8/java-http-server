package com.alpvatansever.httpserver.lib;

import com.alpvatansever.httpserver.lib.http.HttpRequest;
import com.alpvatansever.httpserver.lib.http.HttpResponse;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Server implements AutoCloseable {
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private ExecutorService executorService;
    private ServerSocket serverSocket;

    public void start(int port) {
        if (isRunning.get()) {
            System.out.println("Server is already running");
            return;
        }

        isRunning.set(true);
        executorService = Executors.newFixedThreadPool(10);
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (isRunning.get()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            if (isRunning.get()) {
                System.out.println("Server error: " + e.getMessage());
            }
        } finally {
            close();
        }
    }

    private void handleClient(Socket client) {
        executorService.execute(() -> {
            try (client;
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), UTF_8));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), UTF_8))) {

                String requestLine = in.readLine();
                if (requestLine == null || requestLine.isBlank()) return;

                System.out.println("Request: " + requestLine);
                HttpRequest request = HttpRequest.parse(requestLine, in);
                HttpResponse response = new HttpResponse(out);

                FrontController.handle(request, response);

                // Send the response if no handler has done so yet (404, 405, 500)
                response.flush();

            } catch (Exception e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        });
    }

    @Override
    public void close() {
        if (isRunning.compareAndSet(true, false)) {
            System.out.println("Shutting down server...");

            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing server socket");
            }

            if (executorService != null) {
                executorService.shutdown();
            }

            System.out.println("Server stopped.");
        }
    }
}
