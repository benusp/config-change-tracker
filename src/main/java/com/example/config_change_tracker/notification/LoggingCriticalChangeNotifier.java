package com.example.config_change_tracker.notification;

import com.example.config_change_tracker.domain.ConfigChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingCriticalChangeNotifier
        implements CriticalChangeNotifier {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingCriticalChangeNotifier.class);

    @Override
    public void notify(ConfigChange change) {
        log.warn(
                "CRITICAL CONFIG CHANGE: id={}, rule={}, type={}, by={}",
                change.id(),
                change.ruleType(),
                change.changeType(),
                change.changedBy()
        );
    }
}
