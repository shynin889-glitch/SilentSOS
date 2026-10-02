package org.silentsos.notifiers;

import org.silentsos.model.Contact;
import org.silentsos.model.SOSPacket;

/**
 * Common interface demonstrating ABSTRACTION.
 * Decouples the notification mechanism from the central SOS coordinator.
 */
public interface AlertNotifier {
    /**
     * Dispatches an emergency alert to a specific contact.
     * @param packet Distress metadata
     * @param contact Target recipient
     * @return true if successful, false otherwise
     */
    boolean sendAlert(SOSPacket packet, Contact contact);

    /**
     * Gets the human-readable name of the channel.
     */
    String getChannelName();
}
