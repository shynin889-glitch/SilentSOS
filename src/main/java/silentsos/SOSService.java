package silentos;

import org.springframework.stereotype.Service;

@Service
public class SOSService {

    private SOS sos = new SOS();

    public String activateSOS() {
        sos.activateSOS();
        return "SOS ACTIVATED";
    }

    public String deactivateSOS() {
        sos.deactivateSOS();
        return "SOS DEACTIVATED";
    }

    public String detectPattern(String pattern) {

        sos.setPattern(pattern);

        if (pattern.equals("TAP-TAP-TAP")) {
            sos.activateSOS();
            return "Emergency pattern detected. SOS ACTIVATED";
        }

        return "Pattern not recognized";
    }

    public String getStatus() {
        return sos.getStatus();
    }
}