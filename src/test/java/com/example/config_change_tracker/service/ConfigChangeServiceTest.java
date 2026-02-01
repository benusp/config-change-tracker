package com.example.config_change_tracker.service;

import com.example.config_change_tracker.domain.ChangeType;
import com.example.config_change_tracker.domain.ConfigChange;
import com.example.config_change_tracker.domain.RuleType;
import com.example.config_change_tracker.dto.CreateConfigChangeRequest;
import com.example.config_change_tracker.notification.CriticalChangeNotifier;
import com.example.config_change_tracker.repository.InMemoryConfigChangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigChangeServiceTest {
    private InMemoryConfigChangeRepository repository;
    private CriticalChangeNotifier notifier;
    private ConfigChangeService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryConfigChangeRepository();
        notifier = mock(CriticalChangeNotifier.class);
        service = new ConfigChangeService(repository, notifier);
    }

    @Test
    void createNonCriticalChange_savesWithoutNotification() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangeType(ChangeType.CREATE);
        req.setChangedBy("user");
        req.setAfter("{\"limit\":1000}");
        req.setCritical(false);

        ConfigChange saved = service.createChange(req);

        assertNotNull(saved.id());
        assertNotNull(saved.timestamp());
        assertEquals(req.getRuleType(), saved.ruleType());
        assertEquals(req.getChangeType(), saved.changeType());
        assertEquals(req.getChangedBy(), saved.changedBy());
        assertEquals(req.getAfter(), saved.after());
        assertFalse(saved.critical());

        // notifier should not be called
        verify(notifier, never()).notify(any());
    }

    @Test
    void createCriticalChange_triggersNotification() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.APPROVAL_POLICY);
        req.setChangeType(ChangeType.UPDATE);
        req.setChangedBy("user");
        req.setBefore("{\"approval\":5000}");
        req.setAfter("{\"approval\":10000}");
        req.setCritical(true);

        ConfigChange saved = service.createChange(req);

        assertTrue(saved.critical());
        // capture argument passed to notifier
        ArgumentCaptor<ConfigChange> captor = ArgumentCaptor.forClass(ConfigChange.class);
        verify(notifier, times(1)).notify(captor.capture());

        ConfigChange notifiedConfigChange = captor.getValue();
        assertEquals(saved.id(), notifiedConfigChange.id());
        assertEquals(saved.ruleType(), notifiedConfigChange.ruleType());
    }

    @Test
    void createChange_mapsDtoToDomainCorrectly() {
        CreateConfigChangeRequest req = new CreateConfigChangeRequest();
        req.setRuleType(RuleType.CREDIT_LIMIT);
        req.setChangeType(ChangeType.DELETE);
        req.setChangedBy("carol");
        req.setBefore("{\"discount\":50}");
        req.setCritical(false);

        ConfigChange saved = service.createChange(req);

        assertEquals("carol", saved.changedBy());
        assertEquals("{\"discount\":50}", saved.before());
        assertEquals(RuleType.CREDIT_LIMIT, saved.ruleType());
        assertEquals(ChangeType.DELETE, saved.changeType());
        assertNull(saved.after());
    }
}
