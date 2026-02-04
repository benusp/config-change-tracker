package com.example.config_change_tracker.service;

import com.example.config_change_tracker.domain.ChangeType;
import com.example.config_change_tracker.domain.ConfigChange;
import com.example.config_change_tracker.domain.RuleType;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import com.example.config_change_tracker.notification.CriticalChangeNotifier;
import com.example.config_change_tracker.repository.InMemoryConfigChangeRepository;
import com.example.config_change_tracker.validation.ConfigChangeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfigChangeService {

    private final InMemoryConfigChangeRepository repository;
    private final CriticalChangeNotifier  notifier;
    private final ConfigChangeValidator validator;
    private final Logger log =
            LoggerFactory.getLogger(ConfigChangeService.class);

    public ConfigChangeService(InMemoryConfigChangeRepository repository, CriticalChangeNotifier notifier, ConfigChangeValidator validator) {
        this.repository = repository;
        this.notifier = notifier;
        this.validator = validator;
    }

    public ConfigChange createChange(CreateConfigChangeRequest request) {
        validator.validate(request);

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


        return saved;
    }

    public Optional<ConfigChange> getChangeById(UUID id) {
        return repository.findById(id);
    }

    public List<ConfigChange> listChanges(
            RuleType ruleType,
            ChangeType changeType,
            Instant from,
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
