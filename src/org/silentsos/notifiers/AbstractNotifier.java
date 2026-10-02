package org.silentsos.notifiers;

import org.silentsos.model.Contact;
import org.silentsos.model.SOSPacket;

/**
 * Base class demonstrating INHERITANCE and CODE REUSE.
 * Provides template workflow for formatting and logging before specialized transmission.
 */
public abstract class AbstractNotifier implements AlertNotifier {
    protected final String channelName;

    public AbstractNotifier(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public boolean sendAlert(SOSPacket packet, Contact contact) {
        preDispatch(packet, contact);
        boolean success = doDispatch(packet, contact);
        postDispatch(packet, contact, success);
        return success;
    }

    protected void preDispatch(SOSPacket packet, Contact contact) {
        System.out.printf("[%s] Preparing alert for %s (Priority %d)...\n", 
            channelName, contact.getName(), contact.getPriority());
    }

    /**
     * Subclasses implement their specific protocol/transmission logic.
     * Demonstrates POLYMORPHISM.
     */
    protected abstract boolean doDispatch(SOSPacket packet, Contact contact);

    protected void postDispatch(SOSPacket packet, Contact contact, boolean success) {
        if (success) {
            System.out.printf("[%s] SUCCESS: Dispatched to %s.\n", channelName, contact.getName());
        } else {
            System.err.printf("[%s] FAILURE: Could not dispatch to %s.\n", channelName, contact.getName());
        }
    }
}
