package org.silentsos.notifiers;

import org.silentsos.model.Contact;
import org.silentsos.model.SOSPacket;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Concrete implementation providing local persistence and legal audit trail.
 */
public class AuditLogNotifier extends AbstractNotifier {
    private static final String LOG_FILE = "sos_audit.log";

    public AuditLogNotifier() {
        super("LOCAL-AUDIT-LOG");
    }

    @Override
    protected synchronized boolean doDispatch(SOSPacket packet, Contact contact) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.printf("[%s] [SOS EVENT] Target: %s (%s) | Trigger: %s | Location: %s,%s | IP: %s\n",
                packet.getTimestamp(), contact.getName(), contact.getPhone(),
                packet.getTriggerSource(), packet.getLatitude(), packet.getLongitude(), packet.getIpAddress());
            System.out.printf("  -> Event written to %s successfully.\n", LOG_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("  -> Error writing to audit log: " + e.getMessage());
            return false;
        }
    }
}
