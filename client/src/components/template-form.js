import {css, html, LitElement} from 'lit';
import {ContextConsumer} from '@lit/context';
import {templateContext} from '../context/template-context.js';
import {buttonStyles, formStyles, headerStyles} from '../shared.styles.js';
import {Operand} from '../model/Operand.js';
import {MetalType} from '../model/MetalType.js';
import {EmailTemplate} from '../model/EmailTemplate.js';
import {EmailRecipient} from '../model/EmailRecipient.js';
import {Rule} from '../model/Rule.js';
import './email-item.js';
import './shipping-rule-item.js';

export class TemplateForm extends LitElement {
  static properties = {
    template: {type: Object},
    newEmail: {type: String},
    newRule: {type: Object},
    _initialTemplate: {state: true}
  };

  static styles = [
    buttonStyles,
    formStyles,
    headerStyles,
    css`
        :host {
            display: block;
            background: white;
            padding: 2rem;
            border-radius: 8px;
            border: 1px solid #eee;
        }

        .form-header {
            margin-bottom: 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .section {
            margin-top: 2rem;
            padding-top: 1.5rem;
            border-top: 1px solid #f5f5f5;
        }

        .item-list {
            padding: 0;
            margin: 1rem 0;
        }

        .add-row {
            display: flex;
            gap: 0.5rem;
            margin-top: 1rem;
        }

        .add-row input {
            flex: 1;
        }

        .actions {
            margin-top: 2.5rem;
            display: flex;
            gap: 1rem;
            justify-content: flex-end;
        }

        .unsaved-badge {
            background: #fffbeb;
            color: #b45309;
            padding: 0.25rem 0.6rem;
            border-radius: 99px;
            font-size: 0.75rem;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 0.3rem;
            border: 1px solid #fde68a;
            animation: fadeIn 0.3s ease;
        }

        .error-badge {
            background: #fef2f2;
            color: #b91c1c;
            padding: 0.25rem 0.6rem;
            border-radius: 99px;
            font-size: 0.75rem;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 0.3rem;
            border: 1px solid #fecaca;
            animation: fadeIn 0.3s ease;
        }

        .badge-container {
            display: flex;
            gap: 0.5rem;
            align-items: center;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(-5px); }
            to { opacity: 1; transform: translateY(0); }
        }
    `
  ];

  constructor() {
    super();
    this.template = new EmailTemplate();
    this.newEmail = '';
    this.newRule = new Rule();
    this._initialTemplate = null;

    this._contextConsumer = new ContextConsumer(this, {
      context: templateContext,
      subscribe: true
    });
  }

  willUpdate(changedProperties) {
    if (changedProperties.has('template') && this.template && !this._initialTemplate) {
      this._initialTemplate = JSON.parse(JSON.stringify(this.template));
    }
  }

  _hasChanges() {
    if (!this._initialTemplate || !this.template) return false;
    return JSON.stringify(this.template) !== JSON.stringify(this._initialTemplate);
  }

  _notifyChange() {
    this.dispatchEvent(new CustomEvent('template-changed', {
      detail: { template: this.template },
      bubbles: true,
      composed: true
    }));
  }

  render() {
    const hasUnsavedChanges = this._hasChanges();
    const error = this._contextConsumer?.value?.error;

    return html`
        <div class="form-header">
            <h2>${this.template.id ? 'Edit Template' : 'Create Template'}</h2>
            <div class="badge-container">
                ${error ? html`
                    <div class="error-badge">
                        <span>❌</span> ${error}
                    </div>
                ` : ''}
                ${hasUnsavedChanges ? html`
                    <div class="unsaved-badge">
                        <span>⚠️</span> Unsaved Changes
                    </div>
                ` : ''}
            </div>
        </div>

        <div class="form-group">
            <label>Title</label>
            <input 
                type="text" 
                .value=${this.template.title || ''} 
                @input=${(e) => this._updateField('title', e.target.value)}
                placeholder="Template Title"
            >
        </div>

        <div class="form-group" style="margin-top: 1.5rem;">
            <label>Content</label>
            <textarea 
                rows="6" 
                .value=${this.template.content || ''} 
                @input=${(e) => this._updateField('content', e.target.value)}
                placeholder="Email content..."
            ></textarea>
        </div>

        <div class="section">
            <h3>Recipients</h3>
            <div class="item-list">
                ${(this.template.recipients || []).map((email, index) => html`
                    <email-item 
                      .email=${email} 
                      @remove=${() => this._removeRecipient(index)}
                      @change=${(e) => this._updateRecipient(index, e.detail.value)}
                    ></email-item>
                `)}
            </div>
            <div class="add-row">
                <input 
                    type="email" 
                    .value=${this.newEmail} 
                    @input=${(e) => this.newEmail = e.target.value}
                    placeholder="Enter email address"
                >
                <button class="btn-secondary" @click=${this._addRecipient}>Add Email</button>
            </div>
        </div>

        <div class="section">
            <h3>Shipping Rules</h3>
            <div class="item-list">
                ${(this.template.rules || []).map((rule, index) => html`
                    <shipping-rule-item 
                        .rule=${rule} 
                        @remove=${() => this._removeRule(index)}
                        @change=${(e) => this._updateRule(index, e.detail.value)}
                    ></shipping-rule-item>
                `)}
            </div>
            <div class="add-row">
                <select 
                    .value=${this.newRule.operand}
                    @change=${(e) => {
                        this.newRule = new Rule({...this.newRule, operand: e.target.value});
                    }}
                >
                    ${Object.keys(Operand).map(key => html`<option value="${key}">${key}</option>`)}
                </select>
                <select 
                    .value=${this.newRule.operator}
                    @change=${(e) => this.newRule = {...this.newRule, operator: e.target.value}}
                >
                    ${Rule.getAvailableOperators(this.newRule.operand).map(key => html`<option value="${key}">${key.replace(/_/g, ' ')}</option>`)}
                </select>

                ${this.newRule.operand === 'ITEM' ? html`
                    <select 
                        .value=${this.newRule.metalType}
                        @change=${(e) => this.newRule = {...this.newRule, metalType: e.target.value}}
                        style="flex: 0.5;"
                    >
                        ${Object.keys(MetalType).map(key => html`<option value="${key}">${key}</option>`)}
                    </select>
                ` : html`
                    <input 
                        type="number" 
                        placeholder="Value" 
                        .value=${this.newRule.targetValue}
                        @input=${(e) => this.newRule = {...this.newRule, targetValue: Number(e.target.value)}}
                        style="flex: 0.5;"
                    >
                `}
                <button class="btn-secondary" @click=${this._addRule}>Add Rule</button>
            </div>
        </div>

        <div class="actions">
            <button class="btn-secondary" @click=${this._onCancel}>Cancel</button>
            <button class="btn-primary" @click=${this._onSave}>
                ${this.template.id ? 'Save Changes' : 'Create Template'}
            </button>
        </div>
    `;
  }

  _updateField(field, value) {
    this.template = {...this.template, [field]: value};
    this._notifyChange();
  }

  _addRecipient() {
    if (!this.newEmail) return;
    const recipients = [...(this.template.recipients || []), new EmailRecipient({email: this.newEmail})];
    this.template = {...this.template, recipients};
    this.newEmail = '';
    this._notifyChange();
  }

  _removeRecipient(index) {
    const recipients = [...this.template.recipients];
    recipients.splice(index, 1);
    this.template = {...this.template, recipients};
    this._notifyChange();
  }

  _updateRecipient(index, newValue) {
    const recipients = [...this.template.recipients];
    if (typeof recipients[index] === 'object') {
      recipients[index] = new EmailRecipient({
        ...recipients[index],
        email: newValue
      });
    } else {
      recipients[index] = new EmailRecipient({email: newValue});
    }
    this.template = {...this.template, recipients};
    this._notifyChange();
  }

  _addRule() {
    const rules = [...(this.template.rules || []), new Rule({...this.newRule})];
    this.template = {...this.template, rules};
    this.newRule = new Rule();
    this._notifyChange();
  }

  _updateRule(index, newValue) {
    const rules = [...this.template.rules];
    rules[index] = newValue;
    this.template = {...this.template, rules};
    this._notifyChange();
  }

  _removeRule(index) {
    const rules = [...this.template.rules];
    rules.splice(index, 1);
    this.template = {...this.template, rules};
    this._notifyChange();
  }

  _onSave() {
    this._contextConsumer.value.saveTemplate(this.template.id, this.template);
  }

  _onCancel() {
    this.dispatchEvent(new CustomEvent('cancel-edit', {
      bubbles: true,
      composed: true
    }));
  }
}

customElements.define('template-form', TemplateForm);
