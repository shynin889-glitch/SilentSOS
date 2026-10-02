package com.silentsos;

import com.silentsos.controller.ApiController;
import com.silentsos.service.ContactService;
import com.silentsos.service.SosService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main entry point for Silent-SOS Emergency Response Application.
 * Powered by Java 21 Virtual Threads and lightweight native HTTP server.
 */
public class Main {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            System.out.println("==================================================================");
            System.out.println("   ____  _ _            _         ____   ___  ____   ");
            System.out.println("  / ___|(_) | ___ _ __ | |_      / ___| / _ \\/ ___|  ");
            System.out.println("  \\___ \\| | |/ _ \\ '_ \\| __|____ \\___ \\| | | \\___ \\  ");
            System.out.println("   ___) | | |  __/ | | | |_|_____|___) | |_| |___) | ");
            System.out.println("  |____/|_|_|\\___|_| |_|\\__|     |____/ \\___/|____/  ");
            System.out.println("  Covert Emergency Alert & Personal Safety System (Java 21) ");
            System.out.println("==================================================================");

            // Initialize Services and Controllers
            ContactService contactService = new ContactService();
            SosService sosService = new SosService(contactService);
            ApiController apiController = new ApiController(sosService, contactService);

            // Create HTTP Server
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Utilize Java 21 Virtual Threads for high-throughput, low-latency execution
            server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

            // Register Contexts
            server.createContext("/api", apiController.createApiHandler());
            server.createContext("/", apiController.createStaticHandler());

            server.start();

            System.out.printf(">> Server successfully started on port %d with Java 21 Virtual Threads%n", PORT);
            System.out.println(">> Access Unified Glassmorphic Portal : http://localhost:" + PORT + "/");
            System.out.println(">> System Health Check API             : http://localhost:" + PORT + "/api/system/health");
            System.out.println(">> Press Ctrl+C in terminal to stop server.");
            System.out.println("==================================================================");

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n[Silent-SOS] Gracefully shutting down server...");
                server.stop(1);
                System.out.println("[Silent-SOS] Server stopped.");
            }));

        } catch (IOException e) {
            System.err.println("Failed to start Silent-SOS server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
