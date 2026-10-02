package com.silentsos.service;

import com.silentsos.model.Contact;
import com.silentsos.util.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service managing emergency contacts with persistent file storage.
 */
public class ContactService {
    private static final String DATA_FILE = "data/contacts.json";
    private final List<Contact> contacts = new CopyOnWriteArrayList<>();

    public ContactService() {
        loadContacts();
    }

    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }

    public synchronized Contact addContact(Contact contact) {
        if (contact.getId() == null || contact.getId().trim().isEmpty()) {
            contact.setId("C-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (contact.isPrimary()) {
            // Unmark other primaries if desired or allow multiple
        }
        contacts.add(0, contact);
        saveContacts();
        return contact;
    }

    public synchronized boolean deleteContact(String id) {
        boolean removed = contacts.removeIf(c -> c.getId().equalsIgnoreCase(id));
        if (removed) {
            saveContacts();
        }
        return removed;
    }

    private void loadContacts() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            seedDefaultContacts();
            saveContacts();
            return;
        }

        try {
            String content = Files.readString(Paths.get(DATA_FILE));
            content = content.trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1).trim();
                if (!content.isEmpty()) {
                    // Split JSON objects
                    List<String> objects = splitJsonObjects(content);
                    for (String objStr : objects) {
                        Map<String, Object> map = JsonUtils.parseJsonObject(objStr);
                        Contact c = JsonUtils.mapToContact(map);
                        contacts.add(c);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Silent-SOS] Error loading contacts: " + e.getMessage());
            seedDefaultContacts();
        }
    }

    private void seedDefaultContacts() {
        contacts.clear();
        contacts.add(new Contact("C-001", "Mom (Primary Guardian)", "+91 98765 43210", "guardian.mom@example.com", "Parent", true));
        contacts.add(new Contact("C-002", "National Emergency Police 112", "112", "police.emergency@gov.in", "Emergency Services", true));
        contacts.add(new Contact("C-003", "Rahul (Campus Friend)", "+91 91234 56789", "rahul.friend@college.edu", "Colleague", false));
    }

    private synchronized void saveContacts() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();

            String json = JsonUtils.toJson(contacts);
            Files.writeString(Paths.get(DATA_FILE), json);
        } catch (IOException e) {
            System.err.println("[Silent-SOS] Error saving contacts: " + e.getMessage());
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
