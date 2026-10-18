package com.zenda.demo;

import java.net.InetAddress;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @Value("${app.version:1.0.0}")
    private String version;

    @GetMapping(value = "/", produces = "text/html")
    public String home() throws Exception {
        String pod = InetAddress.getLocalHost().getHostName();
        return "<html><body style='font-family:sans-serif;text-align:center;margin-top:10%'>"
             + "<h1>Zenda CI/CD Demo</h1>"
             + "<p>Version: " + version + "</p>"
             + "<p>Served by pod: <b>" + pod + "</b></p>"
             + "<p><a href='/api/health'>/api/health</a></p>"
             + "</body></html>";
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("version", version);
        body.put("timestamp", Instant.now().toString());
        return body;
    }
}
