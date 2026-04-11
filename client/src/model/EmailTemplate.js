import {EmailRecipient} from './EmailRecipient.js';
import {Rule} from './Rule.js';

/**
 * @typedef {Object} EmailTemplatePayload
 * @property {string} [id]
 * @property {string} [title]
 * @property {string} [content]
 * @property {import('./EmailRecipient').EmailRecipientPayload[]} [recipients]
 * @property {import('./Rule').RulePayload[]} [rules]
 */

export class EmailTemplate {
    /**
     * @param {EmailTemplatePayload} payload
     */
    constructor(payload = {}) {
        this.id = payload.id || null;
        this.title = payload.title || '';
        this.content = payload.content || '';
        this.recipients = (payload.recipients || []).map(r => new EmailRecipient(r));
        this.rules = (payload.rules || []).map(r => new Rule(r));
    }
}
