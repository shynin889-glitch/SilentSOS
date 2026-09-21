package silentos;

import java.time.LocalDateTime;

public class Alert {

    private String message;
    private String location;
    private LocalDateTime dateTime;

    // Constructor
    public Alert(String message, String location) {
        this.message = message;
        this.location = location;
        this.dateTime = LocalDateTime.now();
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    // Display alert details
    public void displayAlert() {
        System.out.println("===== EMERGENCY ALERT =====");
        System.out.println("Message: " + message);
        System.out.println("Location: " + location);
        System.out.println("Date & Time: " + dateTime);
        System.out.println("===========================");
    }
}