package silentos;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sos")
public class SOSController {

    private final SOSService sosService;

    public SOSController(SOSService sosService) {
        this.sosService = sosService;
    }

    @PostMapping("/activate")
    public String activateSOS() {
        return sosService.activateSOS();
    }

    @PostMapping("/deactivate")
    public String deactivateSOS() {
        return sosService.deactivateSOS();
    }

    @PostMapping("/pattern")
    public String detectPattern(@RequestParam String pattern) {
        return sosService.detectPattern(pattern);
    }

    @GetMapping("/status")
    public String getStatus() {
        return sosService.getStatus();
    }
}