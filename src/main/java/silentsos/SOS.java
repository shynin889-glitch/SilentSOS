package silentos;

public class SOS {

    private boolean active;
    private String pattern;
    private String status;

    public SOS() {
        this.active = false;
        this.pattern = "";
        this.status = "INACTIVE";
    }

    public void activateSOS() {
        active = true;
        status = "ACTIVE";
    }

    public void deactivateSOS() {
        active = false;
        status = "INACTIVE";
    }

    public boolean isActive() {
        return active;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getStatus() {
        return status;
    }
}