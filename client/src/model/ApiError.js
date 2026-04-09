/**
 * @typedef {Object} ApiErrorPayload
 * @property {string} errorCode
 * @property {string} message
 * @property {string} path
 * @property {string} traceId
 * @property {string} timestamp
 */

export class BackendApiError extends Error {
    /**
     * @param {ApiErrorPayload} payload
     */
    constructor(payload) {
        super(payload.message || 'Unknown server error');

        this.name = 'BackendApiError';

        this.errorCode = payload.errorCode || 'UNKNOWN_ERROR';
        this.path = payload.path || 'unknown/path';
        this.traceId = payload.traceId || 'no-trace-id';

        this.timestamp = payload.timestamp ? new Date(payload.timestamp) : new Date();
    }
}