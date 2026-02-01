package com.example.config_change_tracker.service;

import com.example.config_change_tracker.domain.ConfigChange;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import com.example.config_change_tracker.repository.InMemoryConfigChangeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ConfigChangeService {

    private final InMemoryConfigChangeRepository repository;

    public ConfigChangeService(InMemoryConfigChangeRepository repository) {
        this.repository = repository;
    }

    public ConfigChange createChange(CreateConfigChangeRequest request) {
        ConfigChange change = new ConfigChange(
                UUID.randomUUID(),
                Instant.now(),
                request.getRuleType(),
                request.getChangeType(),
                request.getChangedBy(),
                request.getBefore(),
                request.getAfter(),
                request.isCritical()
        );
        repository.save(change);
        return change;
    }
}
