package com.pm.preciousmetals.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "email_templates")
@Getter
@Setter
@NoArgsConstructor
public class EmailTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailRecipientEntity> recipients = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailSendingRuleEntity> rules = new ArrayList<>();

    public void addRecipient(EmailRecipientEntity recipient) {
        recipients.add(recipient);
        recipient.setTemplate(this);
    }

    public void addRule(EmailSendingRuleEntity rule) {
        rules.add(rule);
        rule.setTemplate(this);
    }
}
