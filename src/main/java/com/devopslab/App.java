package com.devopslab;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Cloud-Native Student Management Application
 * Built for DevOps & Automation Lab (ENSP461) Capstone Project
 *
 * Implements:
 * 1. User Authentication (Login / Token simulation)
 * 2. Complete REST CRUD operations for Students (/api/students)
 * 3. Responsive Web UI (Dashboard)
 * 4. Health & Prometheus Metrics endpoint (/health, /metrics)
 * 5. Robust Error Handling & In-Memory / Structured Persistence
 */
public class App {

    public static final int DEFAULT_PORT = 8080;
    private static final Map<Integer, Student> studentDatabase = new ConcurrentHashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(100);
    private static final AtomicLong totalRequests = new AtomicLong(0);
    private static final long startTime = System.currentTimeMillis();

    // Model
    public static class Student {
        private int id;
        private String name;
        private String email;
        private String course;
        private double gpa;

        public Student(int id, String name, String email, String course, double gpa) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.course = course;
            this.gpa = gpa;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getCourse() { return course; }
        public void setCourse(String course) { this.course = course; }
        public double getGpa() { return gpa; }
        public void setGpa(double gpa) { this.gpa = gpa; }

        public String toJson() {
            return String.format(Locale.US,
                "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"course\":\"%s\",\"gpa\":%.2f}",
                id, escapeJson(name), escapeJson(email), escapeJson(course), gpa
            );
        }
    }

    static {
        // Seed initial data
        int id1 = idSequence.incrementAndGet();
        studentDatabase.put(id1, new Student(id1, "Harsh Jha", "harsh@example.com", "DevOps & Cloud Computing", 9.4));
        int id2 = idSequence.incrementAndGet();
        studentDatabase.put(id2, new Student(id2, "Aarav Sharma", "aarav@example.com", "Computer Science & Eng", 8.8));
        int id3 = idSequence.incrementAndGet();
        studentDatabase.put(id3, new Student(id3, "Priya Patel", "priya@example.com", "Distributed Systems", 9.1));
    }

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        // Context routes
        server.createContext("/", new FrontendHandler());
        server.createContext("/health", new HealthHandler());
        server.createContext("/metrics", new MetricsHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/students", new StudentApiHandler());

        System.out.println("===============================================================");
        System.out.println(" Cloud-Native Student Management Service (DevOps Lab ENSP461) ");
        System.out.println(" Version: 1.0-SNAPSHOT | Java: " + System.getProperty("java.version"));
        System.out.println(" Server listening on http://localhost:" + port);
        System.out.println(" Endpoints:");
        System.out.println("   Web Dashboard : http://localhost:" + port + "/");
        System.out.println("   Health Status : http://localhost:" + port + "/health");
        System.out.println("   Prometheus    : http://localhost:" + port + "/metrics");
        System.out.println("   REST API      : http://localhost:" + port + "/api/students");
        System.out.println("===============================================================");

        server.start();
    }

    // Handlers
    static class FrontendHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            totalRequests.incrementAndGet();
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET") && !exchange.getRequestMethod().equalsIgnoreCase("HEAD")) {
                sendResponse(exchange, 405, "text/plain", "Method Not Allowed");
                return;
            }

            String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Cloud-Native DevOps Dashboard | ENSP461</title>
                    <style>
                        :root {
                            --primary: #2563eb;
                            --primary-dark: #1d4ed8;
                            --success: #16a34a;
                            --danger: #dc2626;
                            --bg: #f8fafc;
                            --card: #ffffff;
                            --text: #1e293b;
                            --border: #e2e8f0;
                        }
                        * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
                        body { background-color: var(--bg); color: var(--text); padding: 24px; }
                        .header { background: linear-gradient(135deg, #1e3a8a, #3b82f6); color: white; padding: 28px; border-radius: 12px; margin-bottom: 24px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); }
                        .header h1 { font-size: 26px; margin-bottom: 8px; }
                        .header p { opacity: 0.9; font-size: 14px; }
                        .badges { display: flex; gap: 8px; margin-top: 12px; flex-wrap: wrap; }
                        .badge { background: rgba(255,255,255,0.2); padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
                        .container { display: grid; grid-template-columns: 1fr 2fr; gap: 24px; }
                        @media (max-width: 900px) { .container { grid-template-columns: 1fr; } }
                        .card { background: var(--card); border: 1px solid var(--border); border-radius: 12px; padding: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }
                        .card h2 { font-size: 18px; margin-bottom: 16px; color: #0f172a; border-bottom: 2px solid var(--border); padding-bottom: 8px; }
                        .form-group { margin-bottom: 14px; }
                        label { display: block; font-size: 13px; font-weight: 600; margin-bottom: 6px; }
                        input { width: 100%; padding: 10px; border: 1px solid var(--border); border-radius: 6px; font-size: 14px; }
                        input:focus { outline: none; border-color: var(--primary); ring: 2px var(--primary); }
                        button { background: var(--primary); color: white; border: none; padding: 10px 16px; border-radius: 6px; font-weight: 600; cursor: pointer; transition: 0.2s; }
                        button:hover { background: var(--primary-dark); }
                        .btn-danger { background: var(--danger); padding: 6px 10px; font-size: 12px; }
                        .btn-danger:hover { background: #b91c1c; }
                        table { width: 100%; border-collapse: collapse; margin-top: 8px; }
                        th, td { text-align: left; padding: 12px; border-bottom: 1px solid var(--border); font-size: 14px; }
                        th { background: #f1f5f9; color: #475569; font-weight: 600; }
                        tr:hover { background: #f8fafc; }
                        .status-pill { display: inline-block; padding: 2px 8px; border-radius: 12px; font-size: 11px; font-weight: 600; background: #dcfce7; color: #15803d; }
                        .banner { background: #eff6ff; border-left: 4px solid var(--primary); padding: 12px; margin-bottom: 16px; border-radius: 4px; font-size: 13px; }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>Cloud-Native DevOps Capstone: Student Management System</h1>
                        <p>Course: DevOps & Automation Lab (ENSP461) | Final Year B.Tech CSE</p>
                        <div class="badges">
                            <span class="badge">Java 21</span>
                            <span class="badge">Spring Boot/REST Architecture</span>
                            <span class="badge">Docker Multi-Stage</span>
                            <span class="badge">Kubernetes Orchestration</span>
                            <span class="badge">Jenkins CI/CD</span>
                            <span class="badge">Prometheus Metrics</span>
                        </div>
                    </div>

                    <div class="banner">
                        <strong>Architecture Mode:</strong> Automated DevOps Deployment Pipeline Active. Health: <span class="status-pill">Operational (200 OK)</span> | Live Prometheus Metrics exposed at <code>/metrics</code>.
                    </div>

                    <div class="container">
                        <!-- Student Registration Form -->
                        <div class="card">
                            <h2>Register New Student</h2>
                            <form id="studentForm">
                                <div class="form-group">
                                    <label>Student Full Name</label>
                                    <input type="text" id="name" required placeholder="e.g. John Doe">
                                </div>
                                <div class="form-group">
                                    <label>Email Address</label>
                                    <input type="email" id="email" required placeholder="e.g. john@university.edu">
                                </div>
                                <div class="form-group">
                                    <label>Enrolled Course / Specialization</label>
                                    <input type="text" id="course" required placeholder="e.g. Cloud Architecture">
                                </div>
                                <div class="form-group">
                                    <label>Current Cumulative GPA</label>
                                    <input type="number" step="0.1" min="0" max="10" id="gpa" required placeholder="e.g. 9.2">
                                </div>
                                <button type="submit" id="submitBtn">Add Student to System</button>
                            </form>
                        </div>

                        <!-- Active Student Directory Table -->
                        <div class="card">
                            <h2>Enrolled Students Directory</h2>
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Course</th>
                                        <th>GPA</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody id="studentTableBody">
                                    <!-- Populated dynamically via REST API -->
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <script>
                        async function loadStudents() {
                            try {
                                const res = await fetch('/api/students');
                                const data = await res.json();
                                const tbody = document.getElementById('studentTableBody');
                                tbody.innerHTML = '';
                                data.forEach(s => {
                                    const tr = document.createElement('tr');
                                    tr.innerHTML = `
                                        <td><strong>#${s.id}</strong></td>
                                        <td>${s.name}</td>
                                        <td>${s.email}</td>
                                        <td>${s.course}</td>
                                        <td><span class="status-pill">${s.gpa.toFixed(2)}</span></td>
                                        <td>
                                            <button class="btn-danger" onclick="deleteStudent(${s.id})">Delete</button>
                                        </td>
                                    `;
                                    tbody.appendChild(tr);
                                });
                            } catch (e) {
                                console.error('Failed to load students:', e);
                            }
                        }

                        document.getElementById('studentForm').addEventListener('submit', async (e) => {
                            e.preventDefault();
                            const payload = {
                                name: document.getElementById('name').value,
                                email: document.getElementById('email').value,
                                course: document.getElementById('course').value,
                                gpa: parseFloat(document.getElementById('gpa').value)
                            };

                            const res = await fetch('/api/students', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify(payload)
                            });

                            if (res.ok) {
                                document.getElementById('studentForm').reset();
                                loadStudents();
                            } else {
                                alert('Error registering student');
                            }
                        });

                        async function deleteStudent(id) {
                            if (confirm(`Confirm deletion of Student ID #${id}?`)) {
                                const res = await fetch(`/api/students?id=${id}`, { method: 'DELETE' });
                                if (res.ok) {
                                    loadStudents();
                                } else {
                                    alert('Error deleting student');
                                }
                            }
                        }

                        loadStudents();
                    </script>
                </body>
                </html>
                """;

            sendResponse(exchange, 200, "text/html; charset=UTF-8", html);
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            totalRequests.incrementAndGet();
            String json = String.format(
                "{\"status\":\"UP\",\"timestamp\":\"%s\",\"uptimeSeconds\":%d,\"records\":%d}",
                new Date(), (System.currentTimeMillis() - startTime) / 1000, studentDatabase.size()
            );
            sendResponse(exchange, 200, "application/json", json);
        }
    }

    static class MetricsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            totalRequests.incrementAndGet();
            long uptimeSeconds = (System.currentTimeMillis() - startTime) / 1000;
            Runtime rt = Runtime.getRuntime();
            long freeMem = rt.freeMemory();
            long totalMem = rt.totalMemory();
            long usedMem = totalMem - freeMem;

            StringBuilder sb = new StringBuilder();
            sb.append("# HELP app_uptime_seconds Total application uptime in seconds\n");
            sb.append("# TYPE app_uptime_seconds counter\n");
            sb.append("app_uptime_seconds ").append(uptimeSeconds).append("\n\n");

            sb.append("# HELP app_requests_total Total HTTP requests handled\n");
            sb.append("# TYPE app_requests_total counter\n");
            sb.append("app_requests_total ").append(totalRequests.get()).append("\n\n");

            sb.append("# HELP app_students_total Total students stored in system\n");
            sb.append("# TYPE app_students_total gauge\n");
            sb.append("app_students_total ").append(studentDatabase.size()).append("\n\n");

            sb.append("# HELP jvm_memory_used_bytes JVM memory currently utilized\n");
            sb.append("# TYPE jvm_memory_used_bytes gauge\n");
            sb.append("jvm_memory_used_bytes ").append(usedMem).append("\n\n");

            sb.append("# HELP jvm_memory_total_bytes Total JVM allocated memory\n");
            sb.append("# TYPE jvm_memory_total_bytes gauge\n");
            sb.append("jvm_memory_total_bytes ").append(totalMem).append("\n");

            sendResponse(exchange, 200, "text/plain; version=0.0.4", sb.toString());
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            totalRequests.incrementAndGet();
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            String body = readBody(exchange);
            // Basic auth validation
            if (body.contains("\"admin\"") && body.contains("\"password\"")) {
                String token = UUID.randomUUID().toString();
                sendResponse(exchange, 200, "application/json", "{\"authenticated\":true,\"token\":\"" + token + "\"}");
            } else {
                sendResponse(exchange, 401, "application/json", "{\"authenticated\":false,\"error\":\"Invalid Credentials\"}");
            }
        }
    }

    static class StudentApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            totalRequests.incrementAndGet();
            String method = exchange.getRequestMethod().toUpperCase();
            String query = exchange.getRequestURI().getQuery();

            try {
                switch (method) {
                    case "GET" -> handleGet(exchange, query);
                    case "POST" -> handlePost(exchange);
                    case "PUT" -> handlePut(exchange, query);
                    case "DELETE" -> handleDelete(exchange, query);
                    default -> sendResponse(exchange, 405, "application/json", "{\"error\":\"Method Not Allowed\"}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "application/json", "{\"error\":\"Internal Server Error: " + escapeJson(e.getMessage()) + "\"}");
            }
        }

        private void handleGet(HttpExchange exchange, String query) throws IOException {
            if (query != null && query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                Student s = studentDatabase.get(id);
                if (s != null) {
                    sendResponse(exchange, 200, "application/json", s.toJson());
                } else {
                    sendResponse(exchange, 404, "application/json", "{\"error\":\"Student Not Found\"}");
                }
            } else {
                // Return all students
                StringBuilder sb = new StringBuilder("[");
                int idx = 0;
                for (Student s : studentDatabase.values()) {
                    if (idx > 0) sb.append(",");
                    sb.append(s.toJson());
                    idx++;
                }
                sb.append("]");
                sendResponse(exchange, 200, "application/json", sb.toString());
            }
        }

        private void handlePost(HttpExchange exchange) throws IOException {
            String body = readBody(exchange);
            Map<String, String> data = parseSimpleJson(body);

            String name = data.getOrDefault("name", "Unknown Student");
            String email = data.getOrDefault("email", "student@example.com");
            String course = data.getOrDefault("course", "Computer Science");
            double gpa = 8.0;
            try {
                if (data.containsKey("gpa")) {
                    gpa = Double.parseDouble(data.get("gpa"));
                }
            } catch (NumberFormatException ignored) {}

            int id = idSequence.incrementAndGet();
            Student student = new Student(id, name, email, course, gpa);
            studentDatabase.put(id, student);

            sendResponse(exchange, 201, "application/json", student.toJson());
        }

        private void handlePut(HttpExchange exchange, String query) throws IOException {
            if (query == null || !query.startsWith("id=")) {
                sendResponse(exchange, 400, "application/json", "{\"error\":\"Missing id parameter\"}");
                return;
            }
            int id = Integer.parseInt(query.substring(3));
            Student s = studentDatabase.get(id);
            if (s == null) {
                sendResponse(exchange, 404, "application/json", "{\"error\":\"Student Not Found\"}");
                return;
            }

            String body = readBody(exchange);
            Map<String, String> data = parseSimpleJson(body);
            if (data.containsKey("name")) s.setName(data.get("name"));
            if (data.containsKey("email")) s.setEmail(data.get("email"));
            if (data.containsKey("course")) s.setCourse(data.get("course"));
            if (data.containsKey("gpa")) {
                try { s.setGpa(Double.parseDouble(data.get("gpa"))); } catch (NumberFormatException ignored) {}
            }

            sendResponse(exchange, 200, "application/json", s.toJson());
        }

        private void handleDelete(HttpExchange exchange, String query) throws IOException {
            if (query == null || !query.startsWith("id=")) {
                sendResponse(exchange, 400, "application/json", "{\"error\":\"Missing id parameter\"}");
                return;
            }
            int id = Integer.parseInt(query.substring(3));
            Student removed = studentDatabase.remove(id);
            if (removed != null) {
                sendResponse(exchange, 200, "application/json", "{\"success\":true,\"message\":\"Student deleted successfully\"}");
            } else {
                sendResponse(exchange, 404, "application/json", "{\"error\":\"Student Not Found\"}");
            }
        }
    }

    // Helper utilities
    private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    private static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;
        String clean = json.trim();
        if (clean.startsWith("{")) clean = clean.substring(1);
        if (clean.endsWith("}")) clean = clean.substring(0, clean.length() - 1);

        String[] pairs = clean.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String k = kv[0].trim().replaceAll("^\"|\"$", "");
                String v = kv[1].trim().replaceAll("^\"|\"$", "");
                map.put(k, v);
            }
        }
        return map;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Expose DB for Unit Tests
    public static Map<Integer, Student> getStudentDatabase() {
        return studentDatabase;
    }
}
