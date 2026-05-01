import java.io.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * CTDT Agent — runs silently on contributor's PC.
 * On startup: scans ALL existing files in the watched folder and logs them.
 * While running: watches for any file create/modify and logs automatically.
 *
 * Build:   javac CtdtAgent.java
 * Run:     java CtdtAgent
 */
public class CtdtAgent {

    static final String PREFS_NODE = "ctdt_agent";
    static Preferences prefs = Preferences.userRoot().node(PREFS_NODE);

    static String serverUrl;
    static String username;
    static String password;
    static long   projectId;
    static String projectName;
    static String watchFolder;

    // Track recently logged files to avoid duplicate rapid-fire events (debounce)
    static final Map<String, Long> lastLogged = new HashMap<>();
    static final long DEBOUNCE_MS = 1500;

    public static void main(String[] args) throws Exception {
        serverUrl   = prefs.get("serverUrl",   "");
        username    = prefs.get("username",    "");
        password    = prefs.get("password",    "");
        watchFolder = prefs.get("watchFolder", "");
        projectId   = prefs.getLong("projectId", 0L);
        projectName = prefs.get("projectName", "");

        boolean needsSetup = serverUrl.isBlank() || username.isBlank()
                          || password.isBlank()  || watchFolder.isBlank()
                          || projectId == 0L;

        if (needsSetup) {
            runSetup();
        } else {
            System.out.println("[CTDT Agent] Loaded saved settings.");
            System.out.println("[CTDT Agent] User     : " + username);
            System.out.println("[CTDT Agent] Project  : #" + projectId + " — " + projectName);
            System.out.println("[CTDT Agent] Watching : " + watchFolder);
            System.out.println("[CTDT Agent] Server   : " + serverUrl);
            System.out.println("[CTDT Agent] Verifying login...");
            if (!verifyLogin()) {
                System.out.println("[CTDT Agent] Login failed. Re-running setup.");
                prefs.clear();
                runSetup();
            }
        }

        System.out.println("\n[CTDT Agent] Scanning existing files first...");
        initialScan();

        System.out.println("\n[CTDT Agent] Now watching for file changes...");
        watchFolder();
    }

    // ── setup wizard ─────────────────────────────────────────
    static void runSetup() throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== CTDT Agent Setup ===");
        System.out.print("Server URL (e.g. http://192.168.1.5:8080): ");
        serverUrl = sc.nextLine().trim();

        System.out.print("Join Code (given by your project admin): ");
        String joinCode = sc.nextLine().trim();

        System.out.println("Resolving project...");
        long resolvedId = resolveJoinCode(joinCode);
        if (resolvedId <= 0) {
            System.out.println("Invalid Join Code. Contact your admin and try again.");
            System.exit(1);
        }
        projectId = resolvedId;
        System.out.println("Project found: #" + projectId + " — " + projectName);

        System.out.print("Your username: ");
        username = sc.nextLine().trim().toLowerCase();
        System.out.print("Your password: ");
        password = sc.nextLine().trim();

        System.out.println("Verifying login...");
        if (!verifyLogin()) {
            System.out.println("Login failed. Check username/password and try again.");
            prefs.clear(); System.exit(1);
        }

        System.out.print("Folder to watch (e.g. C:\\Projects\\MyApp): ");
        watchFolder = sc.nextLine().trim();

        prefs.put("serverUrl",   serverUrl);
        prefs.put("username",    username);
        prefs.put("password",    password);
        prefs.put("watchFolder", watchFolder);
        prefs.put("projectName", projectName);
        prefs.putLong("projectId", projectId);
        System.out.println("Setup complete. Settings saved.\n");
    }

    // ── initial scan: log ALL files in the watched folder ────
    static void initialScan() {
        Path root = Paths.get(watchFolder);
        if (!Files.exists(root)) {
            System.out.println("[CTDT] Watch folder not found: " + watchFolder);
            return;
        }
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    handleChange(file, true);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println("[CTDT] Initial scan error: " + e.getMessage());
        }
    }

    // ── resolve join code → project ID ───────────────────────
    static long resolveJoinCode(String token) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String url = serverUrl + "/api/projects/join/" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) return -1;
        String body = resp.body();
        long pid = extractLong(body, "projectId");
        projectName = extractString(body, "projectName");
        return pid;
    }

    // ── verify login ──────────────────────────────────────────
    static boolean verifyLogin() throws Exception {
        String body = String.format(
            "{\"username\":\"%s\",\"password\":\"%s\",\"projectId\":%d}",
            username, password, projectId);
        HttpResponse<String> resp = postWithResponse(serverUrl + "/api/agent/login", body);
        if (resp.statusCode() == 200) {
            System.out.println("Login OK.");
            return true;
        }
        System.out.println("Login failed (HTTP " + resp.statusCode() + "): " + resp.body());
        return false;
    }

    // ── watch folder for changes ──────────────────────────────
    static void watchFolder() throws Exception {
        Path root = Paths.get(watchFolder);
        WatchService ws = FileSystems.getDefault().newWatchService();
        registerAll(root, ws);

        while (true) {
            WatchKey key;
            try { key = ws.take(); }
            catch (InterruptedException e) { break; }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) continue;

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path changed = ((Path) key.watchable()).resolve(ev.context());

                if (Files.isDirectory(changed)) {
                    // New directory created — register it for watching
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        registerAll(changed, ws);
                        // Also scan new directory's existing files
                        initialScanDir(changed);
                    }
                } else {
                    // File created or modified — log it
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE
                     || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        handleChange(changed, false);
                    }
                }
            }
            key.reset();
        }
    }

    static void registerAll(Path root, WatchService ws) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                try {
                    dir.register(ws,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY);
                } catch (IOException ignored) {}
                return FileVisitResult.CONTINUE;
            }
        });
    }

    static void initialScanDir(Path dir) {
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    handleChange(file, true);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ignored) {}
    }

    // ── send file log ─────────────────────────────────────────
    static void handleChange(Path file, boolean isInitialScan) {
        try {
            if (!Files.isRegularFile(file)) return;

            String fileName = file.getFileName().toString();

            // Skip hidden / temp / editor swap files
            if (fileName.startsWith(".") || fileName.startsWith("~")
                    || fileName.endsWith(".tmp")  || fileName.endsWith(".swp")
                    || fileName.endsWith(".lock") || fileName.endsWith(".log")
                    || fileName.contains("__pycache__")) return;

            // Debounce: skip if same file was logged within DEBOUNCE_MS
            String absPath = file.toAbsolutePath().toString();
            long now = System.currentTimeMillis();
            Long last = lastLogged.get(absPath);
            if (!isInitialScan && last != null && (now - last) < DEBOUNCE_MS) return;
            lastLogged.put(absPath, now);

            // Count lines; fall back to byte-size estimate for binary files
            long lines;
            try { lines = Files.lines(file, StandardCharsets.UTF_8).count(); }
            catch (Exception e) {
                try { lines = Math.max(1, Files.size(file) / 50); }
                catch (IOException ex) { return; }
            }
            if (lines == 0) return;

            // Build relative path from watched root
            Path root = Paths.get(watchFolder);
            String relPath;
            try { relPath = root.relativize(file).toString().replace("\\", "/"); }
            catch (Exception e) { relPath = fileName; }

            String body = String.format(
                "{\"username\":\"%s\",\"password\":\"%s\",\"projectId\":%d," +
                "\"fileName\":\"%s\",\"contentSize\":%d}",
                username, password, projectId,
                relPath.replace("\\", "/").replace("\"", "\\\""), lines);

            HttpResponse<String> resp = postWithResponse(serverUrl + "/api/agent/log", body);
            if (resp.statusCode() == 200) {
                System.out.println("[CTDT] " + (isInitialScan ? "(scan) " : "") + "Logged: " + relPath + " (" + lines + " lines)");
            } else {
                System.out.println("[CTDT] Failed: " + relPath + " — HTTP " + resp.statusCode() + " " + resp.body());
            }
        } catch (Exception e) {
            System.out.println("[CTDT] Error on " + file + ": " + e.getMessage());
        }
    }

    // ── HTTP ──────────────────────────────────────────────────
    static HttpResponse<String> postWithResponse(String url, String jsonBody) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
            .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // ── minimal JSON field extractors (no external deps) ─────
    static long extractLong(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return -1;
        int colon = json.indexOf(':', idx + search.length());
        if (colon < 0) return -1;
        int start = colon + 1;
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '"')) start++;
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        try { return Long.parseLong(json.substring(start, end)); } catch (Exception e) { return -1; }
    }

    static String extractString(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx < 0) return "";
        int colon = json.indexOf(':', idx + search.length());
        if (colon < 0) return "";
        int open = json.indexOf('"', colon + 1);
        if (open < 0) return "";
        int close = json.indexOf('"', open + 1);
        if (close < 0) return "";
        return json.substring(open + 1, close);
    }
}
