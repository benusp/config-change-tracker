package com.example.config_change_tracker.controller;

import com.example.config_change_tracker.domain.ChangeType;
import com.example.config_change_tracker.domain.ConfigChange;
import com.example.config_change_tracker.domain.RuleType;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import com.example.config_change_tracker.repository.InMemoryConfigChangeRepository;
import com.example.config_change_tracker.service.ConfigChangeService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/config-change")
public class ConfigChangeController {
    private final ConfigChangeService service;
    private final InMemoryConfigChangeRepository repository;

    public ConfigChangeController(ConfigChangeService service, InMemoryConfigChangeRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<ConfigChange> createConfigChange(
            @Valid @RequestBody CreateConfigChangeRequest request) {

        ConfigChange savedChange = service.createChange(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedChange);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfigChange> getById(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ConfigChange> list(
            @RequestParam(required = false) RuleType ruleType,
            @RequestParam(required = false) ChangeType changeType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant to
    ) {
        return repository.findAll().stream()
                .filter(c -> ruleType == null || c.ruleType() == ruleType)
                .filter(c -> changeType == null || c.changeType() == changeType)
                .filter(c -> from == null || !c.timestamp().isBefore(from))
                .filter(c -> to == null || !c.timestamp().isAfter(to))
                .toList();
    }
}
