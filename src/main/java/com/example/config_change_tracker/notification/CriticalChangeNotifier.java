package com.example.config_change_tracker.notification;

import com.example.config_change_tracker.domain.ConfigChange;

public interface CriticalChangeNotifier {
    void notify(ConfigChange change);
}
