package com.gestion.incidents.commentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

@FeignClient(name = "incident-service", url = "${incident.service.url:http://localhost:8082}")
public interface IncidentClient {
    @GetMapping("/api/incidents/existe/{id}")
    boolean incidentExiste(@PathVariable("id") UUID incidentId);
}
