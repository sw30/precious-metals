package com.pm.preciousmetals.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record EmailTemplate(UUID id, String title, String content, List<EmailRecipient> recipients, List<EmailSendingRule> rules) {
    public EmailTemplate {
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(content, "Content cannot be null");
        Objects.requireNonNull(recipients, "Recipients list cannot be null");
        Objects.requireNonNull(rules, "Rules list cannot be null");
    }

    public EmailTemplate withTitle(String newTitle) {
        return new EmailTemplate(id, newTitle, content, recipients, rules);
    }

    public EmailTemplate withContent(String newContent) {
        return new EmailTemplate(id, title, newContent, recipients, rules);
    }

    public EmailTemplate addRecipient(EmailRecipient recipient) {
        List<EmailRecipient> newRecipients = new ArrayList<>(recipients);
        newRecipients.add(recipient);
        return new EmailTemplate(id, title, content, List.copyOf(newRecipients), rules);
    }

    public EmailTemplate removeRecipient(EmailRecipient recipient) {
        List<EmailRecipient> newRecipients = new ArrayList<>(recipients);
        newRecipients.remove(recipient);
        return new EmailTemplate(id, title, content, List.copyOf(newRecipients), rules);
    }

    public EmailTemplate addRule(EmailSendingRule rule) {
        List<EmailSendingRule> newRules = new ArrayList<>(rules);
        newRules.add(rule);
        return new EmailTemplate(id, title, content, recipients, List.copyOf(newRules));
    }

    public EmailTemplate removeRule(UUID ruleId) {
        List<EmailSendingRule> newRules = rules.stream()
                .filter(r -> !r.id().equals(ruleId))
                .toList();
        return new EmailTemplate(id, title, content, recipients, newRules);
    }
}
