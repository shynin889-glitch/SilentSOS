package org.silentsos.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Data Transfer Object representing the emergency distress payload.
 * Encapsulates timestamp, trigger mechanism, location coordinates, and message details.
 */
public class SOSPacket {
    private final String timestamp;
    private final String triggerSource;
    private final String latitude;
    private final String longitude;
    private final String message;
    private final String ipAddress;

    public SOSPacket(String triggerSource, String latitude, String longitude, String message, String ipAddress) {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.triggerSource = triggerSource;
        this.latitude = latitude != null ? latitude : "12.9716 N";
        this.longitude = longitude != null ? longitude : "77.5946 E";
        this.message = message != null ? message : "EMERGENCY: User triggered Silent SOS. Immediate assistance required!";
        this.ipAddress = ipAddress != null ? ipAddress : "192.168.1.102";
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTriggerSource() {
        return triggerSource;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getMessage() {
        return message;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getFormattedAlert() {
        return String.format(
            "🚨 [SILENT SOS ALERT]\n" +
            "Time: %s\n" +
            "Source: %s\n" +
            "Coordinates: %s, %s (https://maps.google.com/?q=%s,%s)\n" +
            "IP Address: %s\n" +
            "Message: %s",
            timestamp, triggerSource, latitude, longitude, latitude.replace(" ", ""), longitude.replace(" ", ""), ipAddress, message
        );
    }
}
