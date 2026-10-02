package org.silentsos;

import org.silentsos.core.SOSManager;
import org.silentsos.model.Contact;
import org.silentsos.ui.DecoyCalculatorUI;

import javax.swing.SwingUtilities;
import java.util.Scanner;

/**
 * Main application entry point for Silent SOS project.
 * Demonstrates system initialization and launch options.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   🛡️ SILENT SOS - EMERGENCY DISPATCH SYSTEM    ");
        System.out.println("   Object-Oriented Programming (Java) Project   ");
        System.out.println("=================================================");

        // 1. Initialize Singleton Manager
        SOSManager manager = SOSManager.getInstance();

        // 2. Populate Emergency Contacts (Encapsulated instances)
        manager.addContact(new Contact("Emergency Services", "+919876543210", "police.dispatch@emergency.gov.in", 1));
        manager.addContact(new Contact("Family / Guardian", "+919812345678", "family.emergency@gmail.com", 1));
        manager.addContact(new Contact("Campus Security", "+919800011122", "security@campus.edu", 2));

        System.out.println("[INIT] Loaded " + manager.getContacts().size() + " emergency contacts.");
        System.out.println("[INIT] Notification channels registered: SMS, EMAIL, LOCAL AUDIT LOG.");

        // Check if user specified --cli mode in arguments
        if (args.length > 0 && args[0].equalsIgnoreCase("--cli")) {
            runCliMode(manager);
        } else {
            // Launch Decoy GUI
            System.out.println("[INFO] Launching Decoy Calculator UI...");
            System.out.println("[HINT] In the calculator, enter '911=' or press [Ctrl + Shift + S] to covertly trigger SOS.");
            SwingUtilities.invokeLater(() -> {
                DecoyCalculatorUI gui = new DecoyCalculatorUI();
                gui.setVisible(true);
            });
        }
    }

    private static void runCliMode(SOSManager manager) {
        System.out.println("\n[CLI Mode Activated]");
        System.out.println("Commands:");
        System.out.println("  sos     - Manually fire emergency SOS");
        System.out.println("  list    - View emergency contacts");
        System.out.println("  exit    - Terminate application");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nSilentSOS> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Shutting down Silent SOS engine. Stay safe!");
                manager.shutdown();
                break;
            } else if (input.equalsIgnoreCase("sos")) {
                manager.triggerSilentSOS("CLI Manual Trigger");
            } else if (input.equalsIgnoreCase("list")) {
                System.out.println("\nRegistered Contacts:");
                for (Contact c : manager.getContacts()) {
                    System.out.println("  " + c);
                }
            } else {
                System.out.println("Unknown command. Type 'sos', 'list', or 'exit'.");
            }
        }
        scanner.close();
    }
}
