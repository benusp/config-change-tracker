package com.example.config_change_tracker.validation;

import com.example.config_change_tracker.domain.ChangeType;
import com.example.config_change_tracker.domain.RuleType;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChangePayloadValidatorTest {
    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createChange_validPayload_passesValidation() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangeType(ChangeType.CREATE);
        req.setChangedBy("user");
        req.setBefore(null);
        req.setAfter("{\"limit\":1000}");
        req.setCritical(true);

        Set<ConstraintViolation<CreateConfigChangeRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void createChange_missingAfter_failsValidation() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangeType(ChangeType.CREATE);
        req.setChangedBy("user");
        req.setBefore(null);
        req.setAfter(null);
        req.setCritical(true);

        Set<ConstraintViolation<CreateConfigChangeRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void updateChange_missingBefore_failsValidation() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangeType(ChangeType.UPDATE);
        req.setChangedBy("user");
        req.setBefore(null); // invalid
        req.setAfter("{\"limit\":2000}");
        req.setCritical(true);

        Set<ConstraintViolation<CreateConfigChangeRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void updateChange_missingAfter_failsValidation() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangeType(ChangeType.UPDATE);
        req.setChangedBy("user");
        req.setBefore("{\"limit\":2000}");
        req.setAfter(null); // invalid
        req.setCritical(true);

        Set<ConstraintViolation<CreateConfigChangeRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void updateChange_validPayload_passesValidation() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangeType(ChangeType.UPDATE);
        req.setChangedBy("user");
        req.setBefore("{\"limit\":1000}"); // invalid
        req.setAfter("{\"limit\":2000}");
        req.setCritical(true);

        Set<ConstraintViolation<CreateConfigChangeRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void deleteChange_invalidAfter_failsValidation() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangeType(ChangeType.DELETE);
        req.setChangedBy("user");
        req.setBefore("{\"limit\":2000}");
        req.setAfter("{\"limit\":1000}"); // invalid
        req.setCritical(false);

        Set<ConstraintViolation<CreateConfigChangeRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void deleteChange_validPayload_passesValidation() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangeType(ChangeType.DELETE);
        req.setChangedBy("user");
        req.setBefore("{\"limit\":2000}");
        req.setAfter("{\"limit\":1000}"); // invalid
        req.setCritical(false);

        Set<ConstraintViolation<CreateConfigChangeRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }
}
