import {css, html, LitElement} from 'lit';
import {ContextConsumer} from '@lit/context';
import {templateContext} from '../context/template-context.js';
import {buttonStyles, headerStyles} from '../shared.styles.js';

export class TemplateList extends LitElement {
  constructor() {
    super();
    this._contextConsumer = new ContextConsumer(this, {
      context: templateContext,
      subscribe: true
    });
  }

  static properties = {
    templates: {type: Array}
  };

  static styles = [
    buttonStyles,
    headerStyles,
    css`
        :host {
            display: block;
        }

        .template-card {
            border: 1px solid #eee;
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            border-radius: 8px;
            background: #ffffff;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .template-card:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }

        h3 {
            margin-bottom: 0.75rem;
            font-size: 1.2rem;
        }

        .content-preview {
            color: #555;
            font-size: 0.95rem;
            margin-bottom: 1rem;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .actions {
            margin-top: 1.5rem;
            display: flex;
            gap: 0.75rem;
        }

        .meta {
            font-size: 0.8rem;
            color: #999;
            display: flex;
            gap: 1rem;
        }
    `
  ];

  render() {
    const templates = this._contextConsumer.value?.templates || [];
    if (templates.length === 0) {
      return html`<p>No templates found. Create one!</p>`;
    }

    return html`
        ${templates.map(t => html`
            <div class="template-card">
                <h3>${t.title}</h3>
                <div class="content-preview">
                    ${t.content}
                </div>
                <div class="meta">
                    <span>Recipients: ${t.recipients?.length || 0}</span>
                    <span>Rules: ${t.rules?.length || 0}</span>
                </div>
                <div class="actions">
                    <button class="btn-primary" @click=${() => this._onEdit(t.id)}>Edit</button>
                    <button class="btn-secondary" @click=${() => this._onDelete(t.id)}>Delete</button>
                </div>
            </div>
        `)}
    `;
  }

  _onEdit(id) {
    this.dispatchEvent(new CustomEvent('edit-template', {
      detail: {id},
      bubbles: true,
      composed: true
    }));
  }

  _onDelete(id) {
    if (confirm('Are you sure you want to delete this template?')) {
      this._contextConsumer.value.deleteTemplate(id);
    }
  }
}

customElements.define('template-list', TemplateList);
