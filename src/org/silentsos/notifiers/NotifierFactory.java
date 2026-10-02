package org.silentsos.notifiers;

/**
 * FACTORY PATTERN: Decouples notifier creation from client code.
 */
public class NotifierFactory {
    public enum ChannelType {
        SMS,
        EMAIL,
        AUDIT_LOG
    }

    public static AlertNotifier createNotifier(ChannelType type) {
        switch (type) {
            case SMS:
                return new SMSNotifier();
            case EMAIL:
                return new EmailNotifier();
            case AUDIT_LOG:
                return new AuditLogNotifier();
            default:
                throw new IllegalArgumentException("Unknown notifier type: " + type);
        }
    }
}
