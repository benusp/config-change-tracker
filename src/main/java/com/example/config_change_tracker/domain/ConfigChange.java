package com.example.config_change_tracker.domain;

import java.time.Instant;
import java.util.UUID;

public record ConfigChange (
    UUID id,
    Instant timestamp,
    RuleType ruleType,
    ChangeType changeType,
    String changedBy,
    String before,
    String after,
    boolean critical
) { }

