package com.silentsos.model;

import java.io.Serializable;

/**
 * Model representing an emergency distress alert event.
 */
public class Alert implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String timestamp;
    private String triggerType;
    private String threatLevel;
    private double latitude;
    private double longitude;
    private double accuracyMeters;
    private String address;
    private int batteryLevel;
    private boolean batteryCharging;
    private String userNotes;
    private String audioBase64;
    private String status;
    private String googleMapsUrl;
    private String openStreetMapUrl;
    private String smsPreview;
    private String whatsAppPreview;

    public Alert() {
        this.status = "NEW";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }

    public String getThreatLevel() { return threatLevel; }
    public void setThreatLevel(String threatLevel) { this.threatLevel = threatLevel; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getAccuracyMeters() { return accuracyMeters; }
    public void setAccuracyMeters(double accuracyMeters) { this.accuracyMeters = accuracyMeters; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(int batteryLevel) { this.batteryLevel = batteryLevel; }

    public boolean isBatteryCharging() { return batteryCharging; }
    public void setBatteryCharging(boolean batteryCharging) { this.batteryCharging = batteryCharging; }

    public String getUserNotes() { return userNotes; }
    public void setUserNotes(String userNotes) { this.userNotes = userNotes; }

    public String getAudioBase64() { return audioBase64; }
    public void setAudioBase64(String audioBase64) { this.audioBase64 = audioBase64; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGoogleMapsUrl() { return googleMapsUrl; }
    public void setGoogleMapsUrl(String googleMapsUrl) { this.googleMapsUrl = googleMapsUrl; }

    public String getOpenStreetMapUrl() { return openStreetMapUrl; }
    public void setOpenStreetMapUrl(String openStreetMapUrl) { this.openStreetMapUrl = openStreetMapUrl; }

    public String getSmsPreview() { return smsPreview; }
    public void setSmsPreview(String smsPreview) { this.smsPreview = smsPreview; }

    public String getWhatsAppPreview() { return whatsAppPreview; }
    public void setWhatsAppPreview(String whatsAppPreview) { this.whatsAppPreview = whatsAppPreview; }
}
