import { Operator } from './Operator.js';

/**
 * @typedef {Object} RulePayload
 * @property {import('./Operand').Operand} operand
 * @property {import('./Operator').Operator} operator
 * @property {number} [targetValue]
 * @property {import('./MetalType').MetalType} [metalType]
 */

export class Rule {
    /**
     * @param {RulePayload} payload
     */
    constructor(payload = {}) {
        this.operand = payload.operand || 'ITEM';
        
        const available = Rule.getAvailableOperators(this.operand);
        if (payload.operator && available.includes(payload.operator)) {
            this.operator = payload.operator;
        } else {
            // Domyślny dla PRICE to LESS_THAN, dla innych (ITEM) pierwszy dostępny (IS_EQUAL)
            if (this.operand === 'PRICE' && available.includes('LESS_THAN')) {
                this.operator = 'LESS_THAN';
            } else {
                this.operator = available[0];
            }
        }
        
        this.targetValue = payload.targetValue !== undefined ? payload.targetValue : 0;
        this.metalType = payload.metalType || 'GOLD';
    }

    /**
     * @param {import('./Operand').Operand} operand
     * @returns {import('./Operator').Operator[]}
     */
    static getAvailableOperators(operand) {
        if (operand === 'ITEM') {
            return [Operator.IS_EQUAL, Operator.IS_NOT_EQUAL];
        }
        return Object.keys(Operator);
    }
}