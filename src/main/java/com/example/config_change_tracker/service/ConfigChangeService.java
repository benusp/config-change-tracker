package com.example.config_change_tracker.service;

import com.example.config_change_tracker.domain.ConfigChange;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import com.example.config_change_tracker.notification.CriticalChangeNotifier;
import com.example.config_change_tracker.repository.InMemoryConfigChangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ConfigChangeService {

    private final InMemoryConfigChangeRepository repository;
    private final CriticalChangeNotifier  notifier;
    private final Logger log =
            LoggerFactory.getLogger(ConfigChangeService.class);

    public ConfigChangeService(InMemoryConfigChangeRepository repository, CriticalChangeNotifier notifier) {
        this.repository = repository;
        this.notifier = notifier;
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

        ConfigChange saved = repository.save(change);

        if (saved.critical()) {
            try {
                notifier.notify(saved);
            } catch (Exception e) {
                log.error(
                        "Failed to notify critical config change {}",
                        saved.id(),
                        e
                );
            }
        }


        return change;
    }
}
