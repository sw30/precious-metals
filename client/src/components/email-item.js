import {css, html, LitElement} from 'lit';
import {buttonStyles} from '../shared.styles.js';
import './email-input.js';

export class EmailItem extends LitElement {
  static properties = {
    email: {type: Object},
    isEditing: {type: Boolean, state: true},
    editedEmail: {type: String, state: true},
    _isEmailValid: {type: Boolean, state: true}
  };

  constructor() {
    super();
    this.isEditing = false;
    this.editedEmail = '';
    this._isEmailValid = true;
  }

  static styles = [
    buttonStyles,
    css`
      :host {
        display: flex;
        justify-content: space-between;
        align-items: flex-start; /* Zmienione z center, aby tekst błędu nie przesuwał przycisków */
        padding: 0.5rem 0.75rem;
        background: #f9f9f9;
        margin-bottom: 0.5rem;
        border-radius: 4px;
        font-size: 0.9rem;
        gap: 0.5rem;
      }
      
      .email-text {
        word-break: break-all;
        flex: 1;
        margin-top: 0.4rem; /* Wyrównanie do przycisków akcji */
      }

      email-input {
        flex: 1;
        margin-bottom: 0;
      }
      
      .actions {
        display: flex;
        gap: 0.25rem;
        margin-top: 0.4rem; /* Wyrównanie do linii tekstu wewnątrz email-input/email-text */
      }

      .actions button {
        padding: 0.2rem 0.6rem;
      }
    `
  ];

  render() {
    if (this.isEditing) {
      return html`
          <email-input
                  label="Adres e-mail firmy"
                  .value=${this.editedEmail}
                  .errorMessage="Błędny format. Wymagane np. jan@firma.pl"
                  required
                  ?hideLabel=${true}
                  @email-changed=${this._onEmailChanged}
                  @keydown=${this._onKeyDown}>
          </email-input>
        <div class="actions">
          <button class="btn-primary" @click=${this._onSave} ?disabled=${!this._isEmailValid}>Save</button>
          <button class="btn-secondary" @click=${this._onCancel}>Cancel</button>
        </div>
      `;
    }

    let emailStr = '';
    if (this.email && typeof this.email === 'object') {
      emailStr = this.email.email || JSON.stringify(this.email);
    } else if (typeof this.email === 'string') {
      emailStr = this.email;
    }

    return html`
      <span class="email-text">${emailStr}</span>
      <div class="actions">
        <button class="btn-remove" @click=${this._onEdit}>Edit</button>
        <button class="btn-remove" @click=${this._onRemove}>Remove</button>
      </div>
    `;
  }

  _onEdit() {
    let emailStr = '';
    if (this.email && typeof this.email === 'object') {
      emailStr = this.email.email || '';
    } else if (typeof this.email === 'string') {
      emailStr = this.email;
    }
    this.editedEmail = emailStr;
    this._isEmailValid = !!emailStr;
    this.isEditing = true;
  }

  _onEmailChanged(e) {
    this.editedEmail = e.detail.value;
    this._isEmailValid = e.detail.isValid;
  }

  _onSave() {
    if (!this.editedEmail || !this._isEmailValid) return;
    this.dispatchEvent(new CustomEvent('change', {
      detail: {value: this.editedEmail},
      bubbles: true,
      composed: true
    }));
    this.isEditing = false;
  }

  _onCancel() {
    this.isEditing = false;
  }

  _onKeyDown(e) {
    if (e.key === 'Enter') this._onSave();
    if (e.key === 'Escape') this._onCancel();
  }

  _onRemove() {
    this.dispatchEvent(new CustomEvent('remove', {
      bubbles: true,
      composed: true
    }));
  }
}

customElements.define('email-item', EmailItem);
