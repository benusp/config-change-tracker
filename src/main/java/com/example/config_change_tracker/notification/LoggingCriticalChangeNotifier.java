package com.example.config_change_tracker.notification;

import com.example.config_change_tracker.domain.ConfigChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class LoggingCriticalChangeNotifier
        implements CriticalChangeNotifier {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingCriticalChangeNotifier.class);

    @Override
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public void notify(ConfigChange change) {
        log.warn(
                "CRITICAL CONFIG CHANGE: id={}, rule={}, type={}, by={}",
                change.id(),
                change.ruleType(),
                change.changeType(),
                change.changedBy()
        );

        if (Math.random() < 0.05) { // 5% chance of simulated failure
            log.error("Simulated transient failure in notification system");
            throw new RuntimeException("External notification service temporarily unavailable");
        }

        log.info("Successfully notified external monitoring system for change {}", change.id());
    }

    @Recover
    public void recoverFromNotificationFailure(Exception e, ConfigChange change) {
        log.error(
                "NOTIFICATION FAILURE: Failed to notify after retries for change {}: {}. " +
                        "Manual intervention may be required.",
                change.id(),
                e.getMessage()
        );
    }
}
