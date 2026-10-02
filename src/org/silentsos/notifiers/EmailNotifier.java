package org.silentsos.notifiers;

import org.silentsos.model.Contact;
import org.silentsos.model.SOSPacket;

/**
 * Concrete implementation simulating SMTP Email transmission with full packet details.
 */
public class EmailNotifier extends AbstractNotifier {

    public EmailNotifier() {
        super("EMAIL-SMTP");
    }

    @Override
    protected boolean doDispatch(SOSPacket packet, Contact contact) {
        System.out.printf("  -> Sending Encrypted Email to %s (%s)...\n", contact.getName(), contact.getEmail());
        System.out.println("     Subject: [CRITICAL] SILENT SOS DISTRESS SIGNAL");
        System.out.println("     Body: " + packet.getFormattedAlert().replace("\n", "\n           "));
        try {
            Thread.sleep(150);
        } catch (InterruptedException ignored) {}
        return true;
    }
}
