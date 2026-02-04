package com.example.config_change_tracker.dto;

import com.example.config_change_tracker.domain.ChangeType;
import com.example.config_change_tracker.domain.RuleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateConfigChangeRequest {

    @NotNull
    private RuleType ruleType;
    @NotNull
    private ChangeType changeType;
    @NotBlank
    private String changedBy;
    private String before;
    private String after;
    private boolean critical;

    // Getters
    public RuleType getRuleType() { return ruleType; }
    public ChangeType getChangeType() { return changeType; }
    public String getChangedBy() { return changedBy; }
    public String getBefore() { return before; }
    public String getAfter() { return after; }
    public boolean isCritical() { return critical; }

    // Setters
    public void setRuleType(RuleType ruleType) { this.ruleType = ruleType; }
    public void setChangeType(ChangeType changeType) { this.changeType = changeType; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
    public void setBefore(String before) { this.before = before; }
    public void setAfter(String after) { this.after = after; }
    public void setCritical(boolean critical) { this.critical = critical; }
}

