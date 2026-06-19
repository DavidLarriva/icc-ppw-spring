package ec.edu.ups.icc.fundamentos01;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    @GetMapping("/api/status")
    public Map<String, String> getStatus() {
        return Map.of(
            "service", "Spring Boot API",
            "status", "running",
            "timestamp", Instant.now().toString()
        );
    }
}
