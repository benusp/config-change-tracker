package com.example.config_change_tracker.controller;

import com.example.config_change_tracker.domain.ConfigChange;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import com.example.config_change_tracker.service.ConfigChangeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config-change")
public class ConfigChangeController {
    private final ConfigChangeService service;

    public ConfigChangeController(ConfigChangeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConfigChange> createConfigChange(
            @Valid @RequestBody CreateConfigChangeRequest request) {

        ConfigChange savedChange = service.createChange(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedChange);
    }
}
