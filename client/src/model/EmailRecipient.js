/**
 * @typedef {Object} EmailRecipientPayload
 * @property {string} email
 */

export class EmailRecipient {
    /**
     * @param {EmailRecipientPayload} payload
     */
    constructor(payload = {}) {
        this.email = payload.email || '';
    }
}
