package org.silentsos.notifiers;

import org.silentsos.model.Contact;
import org.silentsos.model.SOSPacket;

/**
 * Concrete implementation simulating GSM / Twilio SMS gateway dispatch.
 */
public class SMSNotifier extends AbstractNotifier {

    public SMSNotifier() {
        super("SMS-GATEWAY");
    }

    @Override
    protected boolean doDispatch(SOSPacket packet, Contact contact) {
        // Simulating network payload transmission
        System.out.printf("  -> Sending SMS to %s (%s)...\n", contact.getName(), contact.getPhone());
        System.out.printf("     Text: \"EMERGENCY! Coordinates: %s, %s. Help needed!\"\n", 
            packet.getLatitude(), packet.getLongitude());
        try {
            Thread.sleep(150); // Simulate brief network latency
        } catch (InterruptedException ignored) {}
        return true;
    }
}
