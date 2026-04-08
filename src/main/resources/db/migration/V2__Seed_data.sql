-- Inicjalizacja przykładowych szablonów e-mail (tylko jeśli ich nie ma)
INSERT INTO email_templates (id, title, content)
SELECT 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Gold Price Alert', 'Cena złota przekroczyła ustalony poziom.'
WHERE NOT EXISTS (SELECT 1 FROM email_templates WHERE id = 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO email_templates (id, title, content)
SELECT 'b1efbc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Silver Price Drop Alert', 'Cena srebra spadła poniżej ustalonego poziomu.'
WHERE NOT EXISTS (SELECT 1 FROM email_templates WHERE id = 'b1efbc99-9c0b-4ef8-bb6d-6bb9bd380a22');

-- Odbiorcy dla szablonu Gold
INSERT INTO email_recipients (id, email, template_id)
SELECT 'c2dfbc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'investor1@example.com', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'
WHERE NOT EXISTS (SELECT 1 FROM email_recipients WHERE id = 'c2dfbc99-9c0b-4ef8-bb6d-6bb9bd380a33');

INSERT INTO email_recipients (id, email, template_id)
SELECT 'd3efbc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'admin@pm-investing.com', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'
WHERE NOT EXISTS (SELECT 1 FROM email_recipients WHERE id = 'd3efbc99-9c0b-4ef8-bb6d-6bb9bd380a44');

-- Reguła dla szablonu Gold (PRICE > 2700)
INSERT INTO email_sending_rules (id, operand, operator, target_value, template_id)
SELECT 'e4ffbc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'PRICE', 'GREATER_THAN', 2700.00, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'
WHERE NOT EXISTS (SELECT 1 FROM email_sending_rules WHERE id = 'e4ffbc99-9c0b-4ef8-bb6d-6bb9bd380a55');

-- Odbiorca dla szablonu Silver
INSERT INTO email_recipients (id, email, template_id)
SELECT 'f5afbc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'silver-watcher@example.com', 'b1efbc99-9c0b-4ef8-bb6d-6bb9bd380a22'
WHERE NOT EXISTS (SELECT 1 FROM email_recipients WHERE id = 'f5afbc99-9c0b-4ef8-bb6d-6bb9bd380a66');

-- Reguła dla szablonu Silver (PRICE < 30)
INSERT INTO email_sending_rules (id, operand, operator, target_value, template_id)
SELECT 'f6bfbc99-9c0b-4ef8-bb6d-6bb9bd380a77', 'PRICE', 'LESS_THAN', 30.00, 'b1efbc99-9c0b-4ef8-bb6d-6bb9bd380a22'
WHERE NOT EXISTS (SELECT 1 FROM email_sending_rules WHERE id = 'f6bfbc99-9c0b-4ef8-bb6d-6bb9bd380a77');

-- Przykładowe sygnały cenowe (historia)
INSERT INTO price_signals (id, price, metal_type, created_at)
SELECT '77aebc99-9c0b-4ef8-bb6d-6bb9bd380a88', 2650.50, 'GOLD', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM price_signals WHERE id = '77aebc99-9c0b-4ef8-bb6d-6bb9bd380a88');

INSERT INTO price_signals (id, price, metal_type, created_at)
SELECT '88bebc99-9c0b-4ef8-bb6d-6bb9bd380a99', 31.20, 'SILVER', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM price_signals WHERE id = '88bebc99-9c0b-4ef8-bb6d-6bb9bd380a99');
