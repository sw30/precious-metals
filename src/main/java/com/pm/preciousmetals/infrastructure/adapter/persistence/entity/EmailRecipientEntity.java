package com.pm.preciousmetals.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "email_recipients")
@Getter
@Setter
@NoArgsConstructor
public class EmailRecipientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private EmailTemplateEntity template;

    public EmailRecipientEntity(String email) {
        this.email = email;
    }
}
