package com.example.config_change_tracker.validation;

import com.example.config_change_tracker.domain.ChangeType;
import com.example.config_change_tracker.domain.RuleType;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigChangeValidatorTest {
    private ConfigChangeValidator validator;

    @BeforeEach
    void setup() {
        validator = new ConfigChangeValidator();
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

        validator.validate(req);
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

        assertThrows(IllegalArgumentException.class, () -> validator.validate(req));
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

        assertThrows(IllegalArgumentException.class, () -> validator.validate(req));
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

        assertThrows(IllegalArgumentException.class, () -> validator.validate(req));
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

        validator.validate(req);
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

        assertThrows(IllegalArgumentException.class, () -> validator.validate(req));
    }
}
