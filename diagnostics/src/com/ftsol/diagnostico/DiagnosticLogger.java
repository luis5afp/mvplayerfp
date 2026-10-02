package com.ftsol.diagnostico;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Diagnóstico local para MV Play.
 * Guarda request/response de API en almacenamiento app-specific.
 * No guarda vídeo/audio completos por defecto.
 */
public final class DiagnosticLogger {
    private static final Object LOCK = new Object();
    private static final long MAX_BODY_BYTES = 5L * 1024L * 1024L;
    private static volatile String sessionId = newSessionId();

    private static final Pattern SECRET_JSON = Pattern.compile(
            "(?i)(\\\"?(?:password|passwd|token|access_token|refresh_token|authorization|cookie|api_key|apikey)\\\"?\\s*[:=]\\s*\\\"?)([^\\\",&\\s}]+)");
    private static final Pattern SECRET_QUERY = Pattern.compile(
            "(?i)([?&](?:password|passwd|token|access_token|refresh_token|api_key|apikey)=)([^&]+)");

    private DiagnosticLogger() {}

    public static String newSession() {
        sessionId = newSessionId();
        return sessionId;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static void logRequest(Context context, String method, String url,
                                  Map<String, String> headers, String body) {
        writeExchange(context, "request", method, url, 0, headers, body, null);
    }

    public static void logResponse(Context context, String method, String url, int status,
                                   Map<String, String> headers, String body) {
        writeExchange(context, "response", method, url, status, headers, body, null);
    }

    public static void logError(Context context, String method, String url, Throwable error) {
        String message = error == null ? "unknown" : error.getClass().getName() + ": " + error.getMessage();
        writeExchange(context, "error", method, url, -1, null, null, message);
    }

    public static void logStream(Context context, String url, Map<String, String> headers,
                                 int status, String note) {
        writeExchange(context, "stream", "GET", url, status, headers, null, note);
    }

    private static void writeExchange(Context context, String kind, String method, String rawUrl,
                                      int status, Map<String, String> headers, String body, String note) {
        if (context == null) return;
        synchronized (LOCK) {
            try {
                String url = redact(rawUrl == null ? "" : rawUrl);
                String group = classify(url);
                File root = diagnosticRoot(context);
                String day = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
                File dir = new File(new File(new File(root, day), sessionId), group);
                if (!dir.exists() && !dir.mkdirs()) return;

                long now = System.currentTimeMillis();
                String stamp = new SimpleDateFormat("HHmmss_SSS", Locale.US).format(new Date(now));
                String ext = extensionFor(body, headers);
                File meta = new File(dir, stamp + "_" + kind + ".json");
                Map<String, String> safeHeaders = sanitizeHeaders(headers);

                StringBuilder json = new StringBuilder();
                json.append("{\n");
                field(json, "time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US).format(new Date(now)), true);
                field(json, "session", sessionId, true);
                field(json, "group", group, true);
                field(json, "kind", kind, true);
                field(json, "method", method == null ? "" : method, true);
                field(json, "url", url, true);
                json.append("  \"status\": ").append(status).append(",\n");
                json.append("  \"headers\": {");
                boolean first = true;
                for (Map.Entry<String, String> e : safeHeaders.entrySet()) {
                    if (!first) json.append(',');
                    json.append("\n    \"").append(escape(e.getKey())).append("\": \"")
                            .append(escape(e.getValue())).append("\"");
                    first = false;
                }
                if (!safeHeaders.isEmpty()) json.append('\n').append("  ");
                json.append("},\n");
                field(json, "note", note == null ? "" : redact(note), false);
                json.append("\n}\n");
                writeText(meta, json.toString());

                if (body != null && !body.isEmpty()) {
                    byte[] bytes = redact(body).getBytes(StandardCharsets.UTF_8);
                    int len = (int)Math.min(bytes.length, MAX_BODY_BYTES);
                    File payload = new File(dir, stamp + "_" + kind + "_body" + ext);
                    try (FileOutputStream out = new FileOutputStream(payload)) {
                        out.write(bytes, 0, len);
                    }
                    if (bytes.length > MAX_BODY_BYTES) {
                        writeText(new File(dir, stamp + "_TRUNCATED.txt"),
                                "Body truncado a " + MAX_BODY_BYTES + " bytes; original=" + bytes.length);
                    }
                }

                appendIndex(root, day, group, kind, method, url, status, stamp);
                prune(root, 14, 300L * 1024L * 1024L);
            } catch (Throwable ignored) {
                // El diagnóstico nunca debe tumbar la app.
            }
        }
    }

    private static File diagnosticRoot(Context context) {
        File ext = context.getExternalFilesDir(null);
        File base = ext != null ? ext : context.getFilesDir();
        File root = new File(base, "diagnostico");
        if (!root.exists()) root.mkdirs();
        return root;
    }

    private static String classify(String url) {
        String u = url == null ? "" : url.toLowerCase(Locale.US);
        if (u.contains("player_api.php")) return "xtream_player_api";
        if (u.contains("xmltv.php") || u.contains("epg")) return "epg_xmltv";
        if (u.contains("get_live") || u.contains("live_stream")) return "live";
        if (u.contains("get_vod") || u.contains("movie")) return "vod";
        if (u.contains("get_series") || u.contains("series")) return "series";
        if (u.contains("whmcs") || u.contains("clientarea")) return "whmcs";
        if (u.contains("vpn")) return "vpn";
        if (looksLikeMedia(u)) return "streams";
        if (u.contains("/api/") || u.contains("api.php")) return "api_app";
        return "other";
    }

    private static boolean looksLikeMedia(String u) {
        return u.contains(".m3u8") || u.contains(".ts") || u.contains(".mp4") ||
                u.contains(".mkv") || u.contains("/live/") || u.contains("/movie/") || u.contains("/series/");
    }

    private static String extensionFor(String body, Map<String, String> headers) {
        String ct = "";
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                if ("content-type".equalsIgnoreCase(e.getKey())) ct = e.getValue() == null ? "" : e.getValue().toLowerCase(Locale.US);
            }
        }
        String b = body == null ? "" : body.trim();
        if (ct.contains("json") || b.startsWith("{") || b.startsWith("[")) return ".json";
        if (ct.contains("xml") || b.startsWith("<?xml") || b.startsWith("<tv")) return ".xml";
        if (ct.contains("mpegurl") || b.startsWith("#EXTM3U")) return ".m3u";
        return ".txt";
    }

    private static Map<String, String> sanitizeHeaders(Map<String, String> headers) {
        Map<String, String> out = new LinkedHashMap<>();
        if (headers == null) return out;
        for (Map.Entry<String, String> e : headers.entrySet()) {
            String k = e.getKey() == null ? "" : e.getKey();
            String v = e.getValue() == null ? "" : e.getValue();
            if (k.equalsIgnoreCase("Authorization") || k.equalsIgnoreCase("Cookie") ||
                    k.equalsIgnoreCase("Set-Cookie") || k.toLowerCase(Locale.US).contains("token")) {
                v = "[REDACTED]";
            } else {
                v = redact(v);
            }
            out.put(k, v);
        }
        return out;
    }

    private static String redact(String s) {
        if (s == null) return "";
        String x = SECRET_QUERY.matcher(s).replaceAll("$1[REDACTED]");
        return SECRET_JSON.matcher(x).replaceAll("$1[REDACTED]");
    }

    private static void appendIndex(File root, String day, String group, String kind,
                                    String method, String url, int status, String stamp) throws Exception {
        File index = new File(root, "index.jsonl");
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(index, true), StandardCharsets.UTF_8))) {
            w.print("{\"day\":\""); w.print(escape(day));
            w.print("\",\"session\":\""); w.print(escape(sessionId));
            w.print("\",\"group\":\""); w.print(escape(group));
            w.print("\",\"kind\":\""); w.print(escape(kind));
            w.print("\",\"method\":\""); w.print(escape(method == null ? "" : method));
            w.print("\",\"url\":\""); w.print(escape(url));
            w.print("\",\"status\":"); w.print(status);
            w.print(",\"stamp\":\""); w.print(escape(stamp));
            w.println("\"}");
        }
    }

    private static void prune(File root, int keepDays, long maxBytes) {
        File[] children = root.listFiles();
        if (children == null) return;
        long cutoff = System.currentTimeMillis() - keepDays * 86400000L;
        for (File f : children) {
            if (f.isDirectory() && f.lastModified() < cutoff) deleteRecursively(f);
        }
        long size = sizeOf(root);
        if (size <= maxBytes) return;
        File oldest = null;
        for (File f : children) {
            if (f.isDirectory() && (oldest == null || f.lastModified() < oldest.lastModified())) oldest = f;
        }
        if (oldest != null) deleteRecursively(oldest);
    }

    private static long sizeOf(File f) {
        if (f == null || !f.exists()) return 0;
        if (f.isFile()) return f.length();
        long n = 0;
        File[] xs = f.listFiles();
        if (xs != null) for (File x : xs) n += sizeOf(x);
        return n;
    }

    private static void deleteRecursively(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] xs = f.listFiles();
            if (xs != null) for (File x : xs) deleteRecursively(x);
        }
        f.delete();
    }

    private static void writeText(File file, String text) throws Exception {
        try (OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            w.write(text == null ? "" : text);
        }
    }

    private static void field(StringBuilder b, String key, String value, boolean comma) {
        b.append("  \"").append(escape(key)).append("\": \"")
                .append(escape(value == null ? "" : value)).append("\"");
        if (comma) b.append(',');
        b.append('\n');
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }

    private static String newSessionId() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
