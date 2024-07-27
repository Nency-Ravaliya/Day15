package com.mycompany.app;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {
        // Create and start the HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/", new HelloWorldHandler());
        server.setExecutor(null); // Creates a default executor
        server.start();
        System.out.println("Server started on port 8081");
    }

    // Define a simple handler that responds with "Hello World!"
    static class HelloWorldHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello World!";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

