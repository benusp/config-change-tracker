package com.example.config_change_tracker.validation;

import com.example.config_change_tracker.domain.ChangeType;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import org.springframework.stereotype.Component;

@Component
public class ConfigChangeValidator {

    public void validate(CreateConfigChangeRequest request) {

        ChangeType type = request.getChangeType();

        switch (type) {
            case CREATE -> validateCreate(request);
            case UPDATE -> validateUpdate(request);
            case DELETE -> validateDelete(request);
            default -> throw new IllegalArgumentException("invalid type of change");
        }
    }

    private void validateCreate(CreateConfigChangeRequest request) {
        if (request.getBefore() != null) {
            throw new IllegalArgumentException(
                    "before must be empty for CREATE request"
            );
        }
        if (request.getAfter() == null) {
            throw new IllegalArgumentException(
                    "after is required for CREATE request"
            );
        }
    }

    private void validateUpdate(CreateConfigChangeRequest request) {
        if (request.getBefore() == null || request.getAfter() == null) {
            throw new IllegalArgumentException(
                    "both before and after are required for UPDATE request"
            );
        }
    }

    private void validateDelete(CreateConfigChangeRequest request) {
        if (request.getBefore() == null) {
            throw new IllegalArgumentException(
                    "before is required for DELETE request"
            );
        }
        if (request.getAfter() != null) {
            throw new IllegalArgumentException(
                    "after must be empty for DELETE request"
            );
        }
    }
}
