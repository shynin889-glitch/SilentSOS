package silentos;

import java.util.ArrayList;

public class AlertManager {

    private ArrayList<Alert> alertHistory;

    // Constructor
    public AlertManager() {
        alertHistory = new ArrayList<>();
    }

    // Create and store emergency alert
    public Alert sendAlert(String location) {

        String message =
                "EMERGENCY! I need help. Please check my location.";

        Alert alert = new Alert(message, location);

        alertHistory.add(alert);

        System.out.println("Emergency alert created successfully.");

        return alert;
    }

    // Show all previous alerts
    public void showAlertHistory() {

        if (alertHistory.isEmpty()) {
            System.out.println("No previous alerts.");
            return;
        }

        System.out.println("\n===== ALERT HISTORY =====");

        for (int i = 0; i < alertHistory.size(); i++) {

            System.out.println("\nAlert " + (i + 1));
            alertHistory.get(i).displayAlert();
        }
    }

    // Get alert history
    public ArrayList<Alert> getAlertHistory() {
        return alertHistory;
    }
}