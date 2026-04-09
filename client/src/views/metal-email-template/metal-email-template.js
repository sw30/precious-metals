import { LitElement, html, css } from 'lit';
import { ContextProvider } from '@lit/context';
import { templateContext } from '../../context/template-context.js';
import { buttonStyles, headerStyles } from '../../shared.styles.js';
import {emailTemplateApi} from "../../api/metalEmailTemplate.js";
import {BackendApiError} from "../../model/ApiError.js";

export class MetalEmailTemplate extends LitElement {
  static properties = {
    contextValue: { type: Object },
    selectedTemplate: { type: Object },
    showForm: { type: Boolean }
  };

  static styles = [
    buttonStyles,
    headerStyles,
    css`
      :host {
        display: block;
      }
      header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 2.5rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid #eee;
      }
      .main-layout {
        display: grid;
        grid-template-columns: 1fr;
        gap: 3rem;
      }
      @media (min-width: 900px) {
        .main-layout {
          grid-template-columns: 1fr 1.2fr;
        }
      }
      .list-section {
        max-height: 85vh;
        overflow-y: auto;
        padding-right: 1.5rem;
      }
      .form-section {
        position: sticky;
        top: 2rem;
        height: fit-content;
      }
      .empty-state {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 400px;
        border: 1px solid #eee;
        background: white;
        border-radius: 8px;
        color: #888;
        text-align: center;
      }
      .empty-state p {
        margin-bottom: 1.5rem;
      }
    `
  ];

  constructor() {
    super();
    this.selectedTemplate = null;
    this.showForm = false;
    
    this.contextValue = {
      templates: [],
      loading: false,
      error: null,
      fetchTemplates: () => this.fetchTemplates(),
      saveTemplate: (id, data) => {

      },
      deleteTemplate: (id) => {

      }
    };

    this._provider = new ContextProvider(this, {
      context: templateContext,
      initialValue: this.contextValue
    });
  }

  connectedCallback() {
    super.connectedCallback();
    this.contextValue.fetchTemplates();
  }

  updateContext(newValues) {
    this.contextValue = { ...this.contextValue, ...newValues };
    this._provider.setValue(this.contextValue);
  }

  async fetchTemplates() {
    this.updateContext({ loading: true, error: null });

    try {
      const templates = await emailTemplateApi.getAll();
      this.updateContext({ templates, loading: false });
    } catch (error) {
      let errorMessage = 'Unknown error occurred while fetching templates.';

      if (error instanceof BackendApiError) {
        console.error(`API Error [${error.errorCode}]: ${error.message} (Trace: ${error.traceId})`);
        errorMessage = `Can't load metal templates: ${error.message}`;
      } else {
        console.error('Network or parsing error:', error);
      }

      this.updateContext({ error: errorMessage, loading: false });
    }
  }

  disconnectedCallback() {
    super.disconnectedCallback();
  }

  render() {
    return html`
      <header>
        <h1>Management</h1>
        <button class="btn-primary">+ Create New</button>
      </header>
      
      <div class="main-layout">
        <div class="list-section">
          ${this.loading && this.templates.length === 0 ? html`<p>Loading templates...</p>` : ''}
          ${this.error ? html`<p style="color: red">Error: ${this.error}</p>` : ''}

          ${this.contextValue.templates.map(t => html`
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
                      <button class="edit-btn">Edit</button>
                      <button class="delete-btn">Delete</button>
                  </div>
              </div>
          `)}
        </div>

        <div class="form-section">
          ${this.showForm ? html`
            Template Form
          ` : html`
            <div class="empty-state">
              <p>Select a template to edit or create a new one</p>
              <button class="btn-primary">Create New</button>
            </div>
          `}
        </div>
      </div>
    `;
  }
}

customElements.define('metal-email-template', MetalEmailTemplate);
