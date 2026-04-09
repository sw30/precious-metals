package com.pm.preciousmetals.domain.model;

import com.pm.preciousmetals.domain.model.rules.Rule;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EmailTemplate {
    private final UUID id;
    private String title;
    private String content;
    private List<EmailRecipient> recipients;
    private List<Rule> rules;

    public EmailTemplate(UUID id, String title, String content, List<EmailRecipient> recipients, List<Rule> rules) {
        this.id = id;
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.content = Objects.requireNonNull(content, "Content cannot be null");
        this.recipients = new ArrayList<>(Objects.requireNonNull(recipients, "Recipients list cannot be null"));
        validateRules(rules);
        this.rules = new ArrayList<>(Objects.requireNonNull(rules, "Rules list cannot be null"));
    }

    public void update(String title, String content, List<EmailRecipient> recipients, List<Rule> rules) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.content = Objects.requireNonNull(content, "Content cannot be null");
        this.recipients = new ArrayList<>(Objects.requireNonNull(recipients, "Recipients list cannot be null"));
        validateRules(rules);
        this.rules = new ArrayList<>(Objects.requireNonNull(rules, "Rules list cannot be null"));
    }

    private void validateRules(List<Rule> rules) {
        if (rules == null) return;
        for (int i = 0; i < rules.size(); i++) {
            for (int j = i + 1; j < rules.size(); j++) {
                if (rules.get(i).isExclusive(rules.get(j))) {
                    throw new IllegalArgumentException("Rules are mutually exclusive and cannot be satisfied together.");
                }
            }
        }
    }

    public UUID id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }

    public List<EmailRecipient> recipients() {
        return List.copyOf(recipients);
    }

    public List<Rule> rules() {
        return List.copyOf(rules);
    }

    public boolean shouldBeSentFor(PriceSignal signal) {
        if (rules.isEmpty()) {
            return false;
        }
        return rules.stream().allMatch(rule -> rule.matches(signal));
    }
}

