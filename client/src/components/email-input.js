import {css, html, LitElement} from 'lit';
import {buttonStyles, formStyles, headerStyles} from "../shared.styles.js";

export class EmailInput extends LitElement {
  static get properties() {
    return {
      value: {type: String, reflect: true},
      label: {type: String},
      errorMessage: {type: String},
      required: {type: Boolean},
      hideLabel: {type: Boolean},

      _isValid: {state: true},
      _isTouched: {state: true}
    };
  }

  static styles = [
    buttonStyles,
    formStyles,
    headerStyles,
    css`
        :host {
            display: block;
            font-family: system-ui, -apple-system, sans-serif;
            margin-bottom: 1rem;
        }

        .form-control {
            display: flex;
            flex-direction: column;
            gap: 0.25rem;
        }

        input:focus {
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

        input[aria-invalid="true"] {
            border-color: #ef4444;
        }

        input[aria-invalid="true"]:focus {
            box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
        }

        .error-text {
            font-size: 0.75rem;
            color: #ef4444;
            min-height: 1.2em;
            margin-top: 0.25rem;
        }
    `];


  constructor() {
    super();
    this.value = '';
    this.label = 'Adres e-mail';
    this.errorMessage = 'Wprowadź poprawny adres e-mail.';
    this.required = false;
    this.hideLabel = false;
    this._isValid = true;
    this._isTouched = false;
  }

  _validateEmail(email) {
    if (!email && !this.required) return true;
    if (!email && this.required) return false;

    const emailRegex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
    return emailRegex.test(email);
  }

  _handleInput(event) {
    this.value = event.target.value;
    this._isValid = this._validateEmail(this.value);

    this.dispatchEvent(new CustomEvent('email-changed', {
      detail: {
        value: this.value,
        isValid: this._isValid
      },
      bubbles: true,
      composed: true
    }));
  }

  _handleBlur() {
    this._isTouched = true;
    this._isValid = this._validateEmail(this.value);
  }

  render() {
    const showError = this._isTouched && !this._isValid;

    return html`
        <div class="form-control">
            ${this.hideLabel ? '' : html`<label for="email-input">${this.label} ${this.required ? '*' : ''}</label>`}
            <input
                    id="email-input"
                    type="email"
                    .value=${this.value}
                    @input=${this._handleInput}
                    @blur=${this._handleBlur}
                    ?required=${this.required}
                    aria-invalid=${showError ? 'true' : 'false'}
                    aria-describedby="email-error"
                    autocomplete="email"
            />
            <div id="email-error" class="error-text" aria-live="polite">
                ${showError ? this.errorMessage : ''}
            </div>
        </div>
    `;
  }
}

customElements.define('email-input', EmailInput);