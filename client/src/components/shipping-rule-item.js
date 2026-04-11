import {css, html, LitElement} from 'lit';
import {buttonStyles} from '../shared.styles.js';
import {Operand} from '../model/Operand.js';
import {MetalType} from '../model/MetalType.js';
import {Rule} from '../model/Rule.js';

export class ShippingRuleItem extends LitElement {
  static properties = {
    rule: {type: Object},
    isEditing: {type: Boolean, state: true},
    editedRule: {type: Object, state: true}
  };

  static styles = [
    buttonStyles,
    css`
      :host {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.5rem 0.75rem;
        background: #f9f9f9;
        margin-bottom: 0.5rem;
        border-radius: 4px;
        font-size: 0.9rem;
      }
      
      .rule-content {
        display: flex;
        gap: 0.5rem;
        align-items: center;
        flex-wrap: wrap;
      }

      select, input {
        padding: 0.25rem;
        border: 1px solid #ddd;
        border-radius: 4px;
        font-family: inherit;
        font-size: 0.85rem;
      }
      
      strong {
        color: #111;
      }

      .actions {
        display: flex;
        gap: 0.25rem;
        margin-left: 0.5rem;
      }

      .actions button {
        padding: 0.2rem 0.6rem;
      }
    `
  ];

  constructor() {
    super();
    this.isEditing = false;
  }

  render() {
    const r = this.rule || {};

    if (this.isEditing) {
      return html`
        <div class="rule-content">
          <select .value=${this.editedRule.operand} @change=${(e) => this._updateEdited('operand', e.target.value)}>
            ${Object.keys(Operand).map(key => html`<option value="${key}">${key}</option>`)}
          </select>
          <select .value=${this.editedRule.operator} @change=${(e) => this._updateEdited('operator', e.target.value)}>
            ${Rule.getAvailableOperators(this.editedRule.operand).map(key => html`<option value="${key}">${key.replace(/_/g, ' ')}</option>`)}
          </select>
          ${this.editedRule.operand === 'ITEM' ? html`
            <select .value=${this.editedRule.metalType} @change=${(e) => this._updateEdited('metalType', e.target.value)}>
                ${Object.keys(MetalType).map(key => html`<option value="${key}">${key}</option>`)}
            </select>
          ` : html`
            <input 
                type="number" 
                .value=${this.editedRule.targetValue} 
                @input=${(e) => this._updateEdited('targetValue', Number(e.target.value))}
                style="width: 60px;"
            >
          `}
        </div>
        <div class="actions">
          <button class="btn-primary" @click=${this._onSave}>Save</button>
          <button class="btn-secondary" @click=${this._onCancel}>Cancel</button>
        </div>
      `;
    }

    const operand = r.operand || '';
    const operator = (r.operator || '').replace(/_/g, ' ');
    const value = r.operand === 'ITEM' ? (r.metalType || '') : (r.targetValue !== undefined ? r.targetValue : '');

    if (operand && operator) {
      return html`
        <div class="rule-content">
          <strong>${operand}:</strong>
          <span>${operator}</span>
          <strong>${value}</strong>
        </div>
        <div class="actions">
          <button class="btn-remove" @click=${this._onEdit}>Edit</button>
          <button class="btn-remove" @click=${this._onRemove}>Remove</button>
        </div>
      `;
    }

    const type = r.type || r.ruleType || r.shippingRuleType || r.name || r.key || r.label || r.rule_type || '';
    const condition = r.condition || r.ruleCondition || r.shippingRuleCondition || r.value || r.content || r.data || r.rule_condition || '';

    if (!type && !condition && Object.keys(r).length > 0) {
      return html`
        <div class="rule-content">
          <code style="font-size: 0.8rem;">${JSON.stringify(r)}</code>
        </div>
        <div class="actions">
          <button class="btn-remove" @click=${this._onEdit}>Edit</button>
          <button class="btn-remove" @click=${this._onRemove}>Remove</button>
        </div>
      `;
    }

    return html`
      <div class="rule-content">
        <strong>${type || 'Rule'}:</strong>
        <span>${condition || '(no value)'}</span>
      </div>
      <div class="actions">
        <button class="btn-remove" @click=${this._onEdit}>Edit</button>
        <button class="btn-remove" @click=${this._onRemove}>Remove</button>
      </div>
    `;
  }

  _onEdit() {
    this.editedRule = new Rule(this.rule || {});
    this.isEditing = true;
  }

  _updateEdited(field, value) {
    this.editedRule = new Rule({...this.editedRule, [field]: value});
  }

  _onSave() {
    this.dispatchEvent(new CustomEvent('change', {
      detail: {value: this.editedRule},
      bubbles: true,
      composed: true
    }));
    this.isEditing = false;
  }

  _onCancel() {
    this.isEditing = false;
  }

  _onRemove() {
    this.dispatchEvent(new CustomEvent('remove', {
      bubbles: true,
      composed: true
    }));
  }
}

customElements.define('shipping-rule-item', ShippingRuleItem);
