import {css, html, LitElement} from 'lit';
import {ContextProvider} from '@lit/context';
import {templateContext} from '../../context/template-context.js';
import {buttonStyles, headerStyles} from '../../shared.styles.js';
import '../../components/template-list.js';
import '../../components/template-form.js';
import {emailTemplateApi} from "../../api/metalEmailTemplate.js";
import {BackendApiError} from "../../model/ApiError.js";
import {EmailTemplate} from "../../model/EmailTemplate.js";

export class MetalEmailTemplate extends LitElement {
  static properties = {
    contextValue: {type: Object},
    selectedTemplate: {type: Object},
    showForm: {type: Boolean}
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
      saveTemplate: (id, data) => this.saveTemplate(id, data),
      deleteTemplate: (id) => this.deleteTemplate(id)
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
    this.contextValue = {...this.contextValue, ...newValues};
    this._provider.setValue(this.contextValue);
  }

  async fetchTemplates() {
    this.updateContext({loading: true, error: null});

    try {
      const templatesData = await emailTemplateApi.getAll();
      const templates = templatesData.map(t => new EmailTemplate(t));
      this.updateContext({templates, loading: false});
    } catch (error) {
      let errorMessage = 'Unknown error occurred while fetching templates.';

      if (error instanceof BackendApiError) {
        console.error(`API Error [${error.errorCode}]: ${error.message} (Trace: ${error.traceId})`);
        errorMessage = `Can't load metal templates: ${error.message}`;
      } else {
        console.error('Network or parsing error:', error);
      }

      this.updateContext({error: errorMessage, loading: false});
    }
  }

  async saveTemplate(id, data) {
    this.updateContext({loading: true, error: null});

    try {
      if (id) {
        await emailTemplateApi.update(id, data);
      } else {
        await emailTemplateApi.create(data);
      }
      await this.fetchTemplates();
      this.showForm = false;
      alert(`Successfully updated template \n\nID: ${this.selectedTemplate.id} \nTitle: ${this.selectedTemplate.title}`)
      this.selectedTemplate = null;
    } catch (error) {
      console.error('Error saving template:', error);
      const message = error instanceof BackendApiError ? error.message : 'Failed to save template';
      this.updateContext({error: message, loading: false});
    }
  }

  async deleteTemplate(id) {
    this.updateContext({loading: true, error: null});

    try {
      await emailTemplateApi.delete(id);
      await this.fetchTemplates();
      if (this.selectedTemplate?.id === id) {
        this.showForm = false;
        this.selectedTemplate = null;
      }
    } catch (error) {
      console.error('Error deleting template:', error);
      const message = error instanceof BackendApiError ? error.message : 'Failed to delete template';
      this.updateContext({error: message, loading: false});
    }
  }

  handleCreateNew() {
    this.selectedTemplate = new EmailTemplate();
    this.showForm = true;
  }

  handleCancel() {
    this.showForm = false;
    this.selectedTemplate = null;
  }

  handleEditTemplate(event) {
    const templateId = event.detail.id;
    const templateData = this.contextValue.templates.find(t => t.id === templateId);
    this.selectedTemplate = new EmailTemplate(JSON.parse(JSON.stringify(templateData)));
    this.showForm = true;
  }

  handleTemplateChanged(event) {
    this.selectedTemplate = event.detail.template;
  }

  disconnectedCallback() {
    super.disconnectedCallback();
  }

  _renderListState() {
    const {loading, templates} = this.contextValue;

    if (loading && templates.length === 0) {
      return html`<p>Loading templates...</p>`;
    }

    if (templates.length === 0) {
      return html`
          <div class="empty-state">No templates found. Create one!</div>`;
    }

    return html`
        <template-list @edit-template=${this.handleEditTemplate}></template-list>`;
  }

  render() {
    return html`
        <header>
            <h1>Management</h1>
            <button class="btn-primary" @click=${this.handleCreateNew}>+ Create New</button>
        </header>

        <div class="main-layout">
            <div class="list-section">
                ${this._renderListState()}
            </div>

            <div class="form-section">
                ${this.showForm ? html`
                    <template-form 
                        .template=${this.selectedTemplate}
                        @cancel-edit=${this.handleCancel}
                        @template-changed=${this.handleTemplateChanged}
                    ></template-form>
                ` : html`
                    <div class="empty-state">
                        <p>Select a template to edit or create a new one</p>
                        <button class="btn-primary" @click=${this.handleCreateNew}>Create New</button>
                    </div>
                `}
            </div>
        </div>
    `;
  }
}

customElements.define('metal-email-template', MetalEmailTemplate);
