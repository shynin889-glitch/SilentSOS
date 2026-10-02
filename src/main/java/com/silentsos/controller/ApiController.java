package com.silentsos.controller;

import com.silentsos.model.Alert;
import com.silentsos.model.Contact;
import com.silentsos.service.ContactService;
import com.silentsos.service.SosService;
import com.silentsos.util.JsonUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * HTTP Router and API Controller handling REST requests and serving static assets.
 */
public class ApiController {
    private final SosService sosService;
    private final ContactService contactService;
    private final long startTime = System.currentTimeMillis();

    public ApiController(SosService sosService, ContactService contactService) {
        this.sosService = sosService;
        this.contactService = contactService;
    }

    public HttpHandler createApiHandler() {
        return exchange -> {
            // Enable CORS for development flexibility
            addCorsHeaders(exchange);

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod().toUpperCase();
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());

            try {
                switch (path) {
                    case "/api/system/health" -> handleHealth(exchange);
                    case "/api/contacts" -> {
                        if ("GET".equals(method)) handleGetContacts(exchange);
                        else if ("POST".equals(method)) handleAddContact(exchange);
                        else if ("DELETE".equals(method)) handleDeleteContact(exchange, queryParams);
                        else sendError(exchange, 405, "Method Not Allowed");
                    }
                    case "/api/sos/trigger" -> {
                        if ("POST".equals(method)) handleTriggerSos(exchange);
                        else sendError(exchange, 405, "Method Not Allowed");
                    }
                    case "/api/sos/alerts" -> {
                        if ("GET".equals(method)) handleGetAlerts(exchange);
                        else sendError(exchange, 405, "Method Not Allowed");
                    }
                    case "/api/sos/status" -> {
                        if ("PUT".equals(method) || "POST".equals(method)) handleUpdateStatus(exchange, queryParams);
                        else sendError(exchange, 405, "Method Not Allowed");
                    }
                    case "/api/sos/stats" -> {
                        if ("GET".equals(method)) handleGetStats(exchange);
                        else sendError(exchange, 405, "Method Not Allowed");
                    }
                    case "/api/sos/test" -> {
                        if ("POST".equals(method)) handleTestAlert(exchange);
                        else sendError(exchange, 405, "Method Not Allowed");
                    }
                    default -> sendError(exchange, 404, "API endpoint not found: " + path);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendError(exchange, 500, "Internal server error: " + e.getMessage());
            }
        };
    }

    public HttpHandler createStaticHandler() {
        return exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            // Security check against directory traversal
            if (path.contains("..")) {
                sendError(exchange, 400, "Bad Request");
                return;
            }

            // Look in local filesystem directory first (src/main/resources/static), then classpath
            Path localFile = Paths.get("src/main/resources/static" + path);
            byte[] fileBytes = null;
            String contentType = getMimeType(path);

            if (Files.exists(localFile) && !Files.isDirectory(localFile)) {
                fileBytes = Files.readAllBytes(localFile);
            } else {
                // Try from classpath
                InputStream is = getClass().getResourceAsStream("/static" + path);
                if (is != null) {
                    fileBytes = is.readAllBytes();
                    is.close();
                }
            }

            if (fileBytes != null) {
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.getResponseHeaders().set("Cache-Control", "no-cache");
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileBytes);
                os.close();
            } else {
                sendError(exchange, 404, "File Not Found: " + path);
            }
        };
    }

    private void handleHealth(HttpExchange exchange) throws IOException {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("project", "Silent-SOS Emergency System");
        health.put("version", "1.0.0");
        health.put("javaVersion", System.getProperty("java.version"));
        health.put("uptimeSeconds", (System.currentTimeMillis() - startTime) / 1000);
        sendJson(exchange, 200, health);
    }

    private void handleGetContacts(HttpExchange exchange) throws IOException {
        List<Contact> contacts = contactService.getAllContacts();
        sendJson(exchange, 200, contacts);
    }

    private void handleAddContact(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, Object> map = JsonUtils.parseJsonObject(body);
        Contact contact = JsonUtils.mapToContact(map);
        Contact saved = contactService.addContact(contact);
        sendJson(exchange, 201, saved);
    }

    private void handleDeleteContact(HttpExchange exchange, Map<String, String> params) throws IOException {
        String id = params.get("id");
        if (id == null || id.isEmpty()) {
            sendError(exchange, 400, "Missing required query parameter: id");
            return;
        }
        boolean removed = contactService.deleteContact(id);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("success", removed);
        resp.put("id", id);
        sendJson(exchange, removed ? 200 : 404, resp);
    }

    private void handleTriggerSos(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, Object> map = JsonUtils.parseJsonObject(body);
        Alert alert = JsonUtils.mapToAlert(map);
        Alert saved = sosService.triggerAlert(alert);
        sendJson(exchange, 201, saved);
    }

    private void handleGetAlerts(HttpExchange exchange) throws IOException {
        List<Alert> alerts = sosService.getAllAlerts();
        sendJson(exchange, 200, alerts);
    }

    private void handleUpdateStatus(HttpExchange exchange, Map<String, String> params) throws IOException {
        String id = params.get("id");
        String status = params.get("status");

        if (id == null || status == null) {
            String body = readBody(exchange);
            Map<String, Object> map = JsonUtils.parseJsonObject(body);
            if (id == null) id = JsonUtils.getString(map, "id", null);
            if (status == null) status = JsonUtils.getString(map, "status", null);
        }

        if (id == null || status == null) {
            sendError(exchange, 400, "Missing required parameters: id, status");
            return;
        }

        boolean updated = sosService.updateStatus(id, status);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("success", updated);
        resp.put("id", id);
        resp.put("status", status.toUpperCase());
        sendJson(exchange, updated ? 200 : 404, resp);
    }

    private void handleGetStats(HttpExchange exchange) throws IOException {
        Map<String, Object> stats = sosService.getStats();
        sendJson(exchange, 200, stats);
    }

    private void handleTestAlert(HttpExchange exchange) throws IOException {
        Alert test = new Alert();
        test.setId("SOS-TEST-" + (100 + new Random().nextInt(900)));
        test.setTriggerType("DEMO_VIVA_TEST");
        test.setThreatLevel("HIGH");
        test.setLatitude(28.6139); // New Delhi India Gate coordinates for test
        test.setLongitude(77.2090);
        test.setAccuracyMeters(5.0);
        test.setAddress("Academic Viva Demonstration Site");
        test.setBatteryLevel(82);
        test.setBatteryCharging(true);
        test.setUserNotes("Simulated test trigger executed from demo panel");
        test.setStatus("NEW");

        Alert saved = sosService.triggerAlert(test);
        sendJson(exchange, 201, saved);
    }

    private String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = is.read(buffer)) != -1) {
            baos.write(buffer, 0, read);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        byte[] jsonBytes = JsonUtils.toJson(data).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, jsonBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(jsonBytes);
        os.close();
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("error", true);
        err.put("status", statusCode);
        err.put("message", message);
        sendJson(exchange, statusCode, err);
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                try {
                    map.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                } catch (Exception ignored) {}
            } else if (kv.length == 1) {
                map.put(kv[0], "");
            }
        }
        return map;
    }

    private String getMimeType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".html")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".ico")) return "image/x-icon";
        if (lower.endsWith(".webm")) return "audio/webm";
        return "text/plain; charset=UTF-8";
    }
}
