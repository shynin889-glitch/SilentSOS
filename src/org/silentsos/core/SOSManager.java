package org.silentsos.core;

import org.silentsos.model.Contact;
import org.silentsos.model.SOSPacket;
import org.silentsos.notifiers.AlertNotifier;
import org.silentsos.notifiers.NotifierFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Core Controller implementing the SINGLETON PATTERN and OBSERVER PATTERN.
 * Coordinates background dispatch of distress messages without blocking the GUI.
 */
public class SOSManager {
    private static SOSManager instance;

    private final List<Contact> contacts;
    private final List<AlertNotifier> notifiers;
    private final ExecutorService threadPool;

    // Interface for Observer Pattern
    public interface SOSObserver {
        void onSOSTriggered(SOSPacket packet);
    }
    private final List<SOSObserver> observers;

    // Private constructor ensures Singleton
    private SOSManager() {
        this.contacts = new ArrayList<>();
        this.notifiers = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.threadPool = Executors.newFixedThreadPool(3);

        // Load default notifiers using Factory Pattern
        notifiers.add(NotifierFactory.createNotifier(NotifierFactory.ChannelType.SMS));
        notifiers.add(NotifierFactory.createNotifier(NotifierFactory.ChannelType.EMAIL));
        notifiers.add(NotifierFactory.createNotifier(NotifierFactory.ChannelType.AUDIT_LOG));
    }

    /**
     * Global access point for Singleton instance.
     */
    public static synchronized SOSManager getInstance() {
        if (instance == null) {
            instance = new SOSManager();
        }
        return instance;
    }

    public synchronized void addContact(Contact contact) {
        contacts.add(contact);
        // Keep sorted by priority ascending (Priority 1 first)
        contacts.sort(Comparator.comparingInt(Contact::getPriority));
    }

    public synchronized List<Contact> getContacts() {
        return Collections.unmodifiableList(new ArrayList<>(contacts));
    }

    public void addObserver(SOSObserver observer) {
        observers.add(observer);
    }

    /**
     * Covert trigger invocation. Runs asynchronously to maintain decoy performance.
     */
    public void triggerSilentSOS(String source) {
        SOSPacket packet = new SOSPacket(
            source,
            "12.9716° N", // Simulated GPS Coordinates (e.g. Bangalore / User Location)
            "77.5946° E",
            "CRITICAL: Silent SOS Covert trigger activated! User requires urgent help.",
            "192.168.1.102"
        );

        // Notify observers
        for (SOSObserver observer : observers) {
            try {
                observer.onSOSTriggered(packet);
            } catch (Exception e) {
                System.err.println("Observer notification error: " + e.getMessage());
            }
        }

        // Asynchronously dispatch to all channels and all emergency contacts
        threadPool.submit(() -> {
            System.out.println("\n=================================================");
            System.out.println("  🚨 [SILENT SOS ENGAGED] DISPATCHING SIGNALS 🚨 ");
            System.out.println("=================================================");
            
            if (contacts.isEmpty()) {
                System.out.println("[WARN] No emergency contacts registered! Only local logging performed.");
            }

            for (Contact contact : contacts) {
                for (AlertNotifier notifier : notifiers) {
                    try {
                        notifier.sendAlert(packet, contact);
                    } catch (Exception e) {
                        System.err.printf("[%s] Error alerting %s: %s\n", 
                            notifier.getChannelName(), contact.getName(), e.getMessage());
                    }
                }
            }
            System.out.println("=================================================\n");
        });
    }

    public void shutdown() {
        threadPool.shutdown();
    }
}
