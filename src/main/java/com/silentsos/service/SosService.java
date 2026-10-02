package com.silentsos.service;

import com.silentsos.model.Alert;
import com.silentsos.model.Contact;
import com.silentsos.util.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service managing emergency alerts, simulated dispatching, and triage operations.
 */
public class SosService {
    private static final String DATA_FILE = "data/alerts.json";
    private final List<Alert> alerts = new CopyOnWriteArrayList<>();
    private final ContactService contactService;

    public SosService(ContactService contactService) {
        this.contactService = contactService;
        loadAlerts();
    }

    public synchronized Alert triggerAlert(Alert alert) {
        if (alert.getId() == null || alert.getId().trim().isEmpty()) {
            alert.setId("SOS-" + (1000 + new Random().nextInt(9000)));
        }
        if (alert.getTimestamp() == null || alert.getTimestamp().trim().isEmpty()) {
            alert.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        if (alert.getStatus() == null || alert.getStatus().trim().isEmpty()) {
            alert.setStatus("NEW");
        }

        // Generate Map Links
        double lat = alert.getLatitude();
        double lng = alert.getLongitude();
        if (lat != 0.0 || lng != 0.0) {
            alert.setGoogleMapsUrl("https://www.google.com/maps?q=" + lat + "," + lng);
            alert.setOpenStreetMapUrl("https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lng + "#map=17/" + lat + "/" + lng);
        } else {
            alert.setGoogleMapsUrl("https://www.google.com/maps");
            alert.setOpenStreetMapUrl("https://www.openstreetmap.org");
        }

        // Generate Dispatch Previews for Guardians
        String mapUrl = alert.getGoogleMapsUrl();
        String sms = String.format(
            "EMERGENCY DISTRESS ALERT! [%s]\n" +
            "Threat Level: %s\n" +
            "Trigger: %s\n" +
            "Battery: %d%%\n" +
            "Live Location: %s\n" +
            "Accuracy: ±%.1fm\n" +
            "Audio evidence recorded. Help required immediately!",
            alert.getId(),
            alert.getThreatLevel(),
            alert.getTriggerType(),
            alert.getBatteryLevel(),
            mapUrl,
            alert.getAccuracyMeters()
        );
        alert.setSmsPreview(sms);

        String whatsapp = String.format(
            "*🚨 SILENT-SOS EMERGENCY BROADCAST 🚨*\n\n" +
            "• *Incident ID:* `%s`\n" +
            "• *Threat Severity:* *%s*\n" +
            "• *Trigger Mode:* %s\n" +
            "• *Device Battery:* %d%%\n" +
            "• *GPS Live Pin:* %s\n" +
            "• *Approx Accuracy:* ±%.1f meters\n\n" +
            "_This is a covert distress signal automatically dispatched by Silent-SOS. Take immediate rescue action._",
            alert.getId(),
            alert.getThreatLevel(),
            alert.getTriggerType(),
            alert.getBatteryLevel(),
            mapUrl,
            alert.getAccuracyMeters()
        );
        alert.setWhatsAppPreview(whatsapp);

        // Prepend new alert to top of list
        alerts.add(0, alert);
        saveAlerts();

        // Simulate multi-channel emergency broadcast
        simulateDispatch(alert);

        return alert;
    }

    public List<Alert> getAllAlerts() {
        return new ArrayList<>(alerts);
    }

    public Alert getAlertById(String id) {
        for (Alert a : alerts) {
            if (a.getId().equalsIgnoreCase(id)) return a;
        }
        return null;
    }

    public synchronized boolean updateStatus(String id, String status) {
        for (Alert a : alerts) {
            if (a.getId().equalsIgnoreCase(id)) {
                a.setStatus(status.toUpperCase());
                saveAlerts();
                System.out.printf("[Silent-SOS] Incident %s status updated to: %s%n", id, status.toUpperCase());
                return true;
            }
        }
        return false;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        int total = alerts.size();
        int active = 0;
        int resolved = 0;
        int acknowledged = 0;

        for (Alert a : alerts) {
            String st = a.getStatus();
            if ("RESOLVED".equalsIgnoreCase(st) || "FALSE_ALARM".equalsIgnoreCase(st)) {
                resolved++;
            } else if ("ACKNOWLEDGED".equalsIgnoreCase(st) || "DISPATCHED".equalsIgnoreCase(st)) {
                acknowledged++;
                active++;
            } else {
                active++;
            }
        }

        stats.put("totalAlerts", total);
        stats.put("activeAlerts", active);
        stats.put("acknowledgedAlerts", acknowledged);
        stats.put("resolvedAlerts", resolved);
        stats.put("totalContacts", contactService.getAllContacts().size());
        stats.put("lastTriggerTime", alerts.isEmpty() ? "None" : alerts.get(0).getTimestamp());
        return stats;
    }

    private void simulateDispatch(Alert alert) {
        System.out.println("\n=======================================================");
        System.out.println("🚨 [SILENT-SOS ALERT ENGINE] EMERGENCY TRIGGER ACTIVATED!");
        System.out.println("=======================================================");
        System.out.println("Incident ID   : " + alert.getId());
        System.out.println("Threat Level  : " + alert.getThreatLevel());
        System.out.println("Trigger Method: " + alert.getTriggerType());
        System.out.println("Coordinates   : " + alert.getLatitude() + ", " + alert.getLongitude());
        System.out.println("Live Map Link : " + alert.getGoogleMapsUrl());
        System.out.println("Audio Attached: " + (alert.getAudioBase64() != null && !alert.getAudioBase64().isEmpty() ? "YES (Ambient Audio Recording)" : "NO"));
        System.out.println("\n--- [DISPATCHING TO REGISTERED EMERGENCY CONTACTS] ---");

        List<Contact> contacts = contactService.getAllContacts();
        if (contacts.isEmpty()) {
            System.out.println("  ⚠️ No contacts registered. Simulating default broadcast to Police (112).");
        } else {
            for (Contact c : contacts) {
                System.out.printf("  -> [SIMULATED SMS / WA SENT] To %s (%s, %s): %s%n",
                        c.getName(), c.getRelationship(), c.getPhone(),
                        alert.getThreatLevel() + " Distress at " + alert.getGoogleMapsUrl());
            }
        }
        System.out.println("=======================================================\n");
    }

    private void loadAlerts() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            seedSampleAlerts();
            saveAlerts();
            return;
        }

        try {
            String content = Files.readString(Paths.get(DATA_FILE)).trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1).trim();
                if (!content.isEmpty()) {
                    List<String> objects = splitJsonObjects(content);
                    for (String objStr : objects) {
                        Map<String, Object> map = JsonUtils.parseJsonObject(objStr);
                        Alert a = JsonUtils.mapToAlert(map);
                        alerts.add(a);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Silent-SOS] Error loading alerts: " + e.getMessage());
            seedSampleAlerts();
        }
    }

    private void seedSampleAlerts() {
        alerts.clear();
        Alert sample1 = new Alert();
        sample1.setId("SOS-1042");
        sample1.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis() - 1000 * 60 * 25)));
        sample1.setTriggerType("COVERT_CALCULATOR");
        sample1.setThreatLevel("CRITICAL");
        sample1.setLatitude(12.9716);
        sample1.setLongitude(77.5946);
        sample1.setAccuracyMeters(8.5);
        sample1.setAddress("MG Road Metro Station, Bangalore");
        sample1.setBatteryLevel(24);
        sample1.setBatteryCharging(false);
        sample1.setUserNotes("Covert trigger activated via Calculator PIN '999='");
        sample1.setStatus("DISPATCHED");
        sample1.setGoogleMapsUrl("https://www.google.com/maps?q=12.9716,77.5946");
        sample1.setOpenStreetMapUrl("https://www.openstreetmap.org/?mlat=12.9716&mlon=77.5946#map=16/12.9716/77.5946");
        sample1.setSmsPreview("EMERGENCY DISTRESS ALERT! [SOS-1042]\nThreat Level: CRITICAL\nLocation: https://www.google.com/maps?q=12.9716,77.5946");
        sample1.setWhatsAppPreview("*🚨 SILENT-SOS ALERT SOS-1042 🚨*\nThreat: CRITICAL\nLive Map: https://www.google.com/maps?q=12.9716,77.5946");
        alerts.add(sample1);
    }

    private synchronized void saveAlerts() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();

            String json = JsonUtils.toJson(alerts);
            Files.writeString(Paths.get(DATA_FILE), json);
        } catch (IOException e) {
            System.err.println("[Silent-SOS] Error saving alerts: " + e.getMessage());
        }
    }

    private List<String> splitJsonObjects(String content) {
        List<String> list = new ArrayList<>();
        int depth = 0;
        int start = -1;
        boolean inString = false;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                inString = !inString;
            } else if (!inString) {
                if (c == '{') {
                    if (depth == 0) start = i;
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0 && start != -1) {
                        list.add(content.substring(start, i + 1));
                        start = -1;
                    }
                }
            }
        }
        return list;
    }
}
