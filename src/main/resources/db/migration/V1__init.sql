CREATE TABLE price_signals (
    id UUID PRIMARY KEY,
    price DECIMAL(19, 2) NOT NULL,
    metal_type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE email_templates (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content VARCHAR(1000) NOT NULL
);

CREATE TABLE email_recipients (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    template_id UUID,
    CONSTRAINT fk_recipient_template FOREIGN KEY (template_id) REFERENCES email_templates(id)
);

CREATE TABLE email_sending_rules (
    id UUID PRIMARY KEY,
    operand VARCHAR(255) NOT NULL,
    operator VARCHAR(255) NOT NULL,
    target_value DECIMAL(19, 2) NOT NULL,
    template_id UUID NOT NULL,
    CONSTRAINT fk_rule_template FOREIGN KEY (template_id) REFERENCES email_templates(id)
);
