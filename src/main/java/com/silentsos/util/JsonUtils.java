package com.silentsos.util;

import com.silentsos.model.Alert;
import com.silentsos.model.Contact;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight, zero-dependency JSON utility for parsing requests and serializing responses.
 * Avoids any external JAR dependencies so the project works out of the box in VS Code.
 */
public class JsonUtils {

    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escapeJson((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(String.valueOf(entry.getKey()))).append("\":");
                sb.append(toJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) obj;
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : col) {
                if (!first) sb.append(",");
                sb.append(toJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }

        // POJO Serialization via reflection
        StringBuilder sb = new StringBuilder("{");
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean first = true;
        for (Field f : fields) {
            if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
            f.setAccessible(true);
            try {
                Object val = f.get(obj);
                if (!first) sb.append(",");
                sb.append("\"").append(f.getName()).append("\":");
                sb.append(toJson(val));
                first = false;
            } catch (IllegalAccessException ignored) {}
        }
        sb.append("}");
        return sb.toString();
    }

    public static String escapeJson(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        String hex = String.format("\\u%04x", (int) c);
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Parses a simple flat or 1-level nested JSON string into a Map of key-value strings/objects.
     */
    public static Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (json == null || json.trim().isEmpty()) return map;

        String s = json.trim();
        if (s.startsWith("{")) s = s.substring(1);
        if (s.endsWith("}")) s = s.substring(0, s.length() - 1);
        s = s.trim();

        if (s.isEmpty()) return map;

        int length = s.length();
        int i = 0;
        while (i < length) {
            // Find key
            while (i < length && (Character.isWhitespace(s.charAt(i)) || s.charAt(i) == ',')) i++;
            if (i >= length) break;

            if (s.charAt(i) != '"') {
                i++;
                continue;
            }
            i++; // skip opening quote
            int keyStart = i;
            while (i < length && s.charAt(i) != '"') {
                if (s.charAt(i) == '\\') i++;
                i++;
            }
            String key = s.substring(keyStart, i);
            i++; // skip closing quote

            // Find colon
            while (i < length && s.charAt(i) != ':') i++;
            i++; // skip colon

            while (i < length && Character.isWhitespace(s.charAt(i))) i++;
            if (i >= length) break;

            // Find value
            char firstChar = s.charAt(i);
            if (firstChar == '"') {
                i++;
                int valStart = i;
                StringBuilder valSb = new StringBuilder();
                while (i < length) {
                    char c = s.charAt(i);
                    if (c == '\\' && i + 1 < length) {
                        char next = s.charAt(i + 1);
                        switch (next) {
                            case '"' -> valSb.append('"');
                            case '\\' -> valSb.append('\\');
                            case 'n' -> valSb.append('\n');
                            case 'r' -> valSb.append('\r');
                            case 't' -> valSb.append('\t');
                            default -> valSb.append(next);
                        }
                        i += 2;
                    } else if (c == '"') {
                        break;
                    } else {
                        valSb.append(c);
                        i++;
                    }
                }
                map.put(key, valSb.toString());
                i++; // skip closing quote
            } else if (firstChar == '{' || firstChar == '[') {
                char open = firstChar;
                char close = (open == '{') ? '}' : ']';
                int depth = 0;
                int start = i;
                boolean inString = false;
                while (i < length) {
                    char c = s.charAt(i);
                    if (c == '"' && (i == 0 || s.charAt(i - 1) != '\\')) {
                        inString = !inString;
                    } else if (!inString) {
                        if (c == open) depth++;
                        else if (c == close) {
                            depth--;
                            if (depth == 0) {
                                i++;
                                break;
                            }
                        }
                    }
                    i++;
                }
                map.put(key, s.substring(start, i));
            } else {
                int start = i;
                while (i < length && s.charAt(i) != ',' && s.charAt(i) != '}') {
                    i++;
                }
                String token = s.substring(start, i).trim();
                if ("true".equalsIgnoreCase(token)) {
                    map.put(key, Boolean.TRUE);
                } else if ("false".equalsIgnoreCase(token)) {
                    map.put(key, Boolean.FALSE);
                } else if ("null".equalsIgnoreCase(token)) {
                    map.put(key, null);
                } else {
                    try {
                        if (token.contains(".")) {
                            map.put(key, Double.parseDouble(token));
                        } else {
                            map.put(key, Long.parseLong(token));
                        }
                    } catch (NumberFormatException e) {
                        map.put(key, token);
                    }
                }
            }
        }

        return map;
    }

    public static Contact mapToContact(Map<String, Object> map) {
        Contact c = new Contact();
        c.setId(getString(map, "id", UUID.randomUUID().toString().substring(0, 8)));
        c.setName(getString(map, "name", "Emergency Contact"));
        c.setPhone(getString(map, "phone", ""));
        c.setEmail(getString(map, "email", ""));
        c.setRelationship(getString(map, "relationship", "Guardian"));
        Object prim = map.get("primary");
        if (prim instanceof Boolean) c.setPrimary((Boolean) prim);
        else if (prim != null) c.setPrimary(Boolean.parseBoolean(prim.toString()));
        return c;
    }

    public static Alert mapToAlert(Map<String, Object> map) {
        Alert a = new Alert();
        a.setId(getString(map, "id", "SOS-" + (1000 + new Random().nextInt(9000))));
        a.setTimestamp(getString(map, "timestamp", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        a.setTriggerType(getString(map, "triggerType", "COVERT_TRIGGER"));
        a.setThreatLevel(getString(map, "threatLevel", "CRITICAL"));
        a.setLatitude(getDouble(map, "latitude", 0.0));
        a.setLongitude(getDouble(map, "longitude", 0.0));
        a.setAccuracyMeters(getDouble(map, "accuracyMeters", 10.0));
        a.setAddress(getString(map, "address", "GPS Coordinates Captured"));
        a.setBatteryLevel(getInt(map, "batteryLevel", 100));
        Object batChg = map.get("batteryCharging");
        if (batChg instanceof Boolean) a.setBatteryCharging((Boolean) batChg);
        a.setUserNotes(getString(map, "userNotes", "Silent SOS triggered by user"));
        a.setAudioBase64(getString(map, "audioBase64", ""));
        a.setStatus(getString(map, "status", "NEW"));
        return a;
    }

    public static String getString(Map<String, Object> map, String key, String def) {
        Object v = map.get(key);
        return v == null ? def : v.toString();
    }

    public static double getDouble(Map<String, Object> map, String key, double def) {
        Object v = map.get(key);
        if (v == null) return def;
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return def; }
    }

    public static int getInt(Map<String, Object> map, String key, int def) {
        Object v = map.get(key);
        if (v == null) return def;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return def; }
    }
}
