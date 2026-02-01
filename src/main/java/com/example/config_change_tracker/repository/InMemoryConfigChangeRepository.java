package com.example.config_change_tracker.repository;

import com.example.config_change_tracker.domain.ConfigChange;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryConfigChangeRepository {

    private final Map<UUID, ConfigChange> storage = new ConcurrentHashMap<>();

    public ConfigChange save(ConfigChange change) {
        storage.put(change.id(), change);
        return change;
    }

    public Optional<ConfigChange> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<ConfigChange> findAll() {
        return new ArrayList<>(storage.values());
    }
}
