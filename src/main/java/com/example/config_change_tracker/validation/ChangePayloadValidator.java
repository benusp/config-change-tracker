package com.example.config_change_tracker.validation;

import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChangePayloadValidator
        implements ConstraintValidator<ValidChangePayload, CreateConfigChangeRequest> {

    @Override
    public boolean isValid(CreateConfigChangeRequest req,
                           ConstraintValidatorContext context) {

        if (req == null || req.getChangeType() == null) {
            return true; // let @NotNull handle it
        }

        return switch (req.getChangeType()) {
            case CREATE -> req.getBefore() == null && req.getAfter() != null;
            case UPDATE -> req.getBefore() != null && req.getAfter() != null;
            case DELETE -> req.getBefore() != null && req.getAfter() == null;
        };
    }
}
