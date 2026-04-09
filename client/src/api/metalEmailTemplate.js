import {apiFetch} from "./base.js";

const METAL_EMAIL_TEMPLATES_API_URL = '/email-templates';

export const emailTemplateApi = {
    getAll: () => apiFetch(METAL_EMAIL_TEMPLATES_API_URL),
    getById: (id) => apiFetch(`${METAL_EMAIL_TEMPLATES_API_URL}/${id}`),
    create: (template) => apiFetch(`${METAL_EMAIL_TEMPLATES_API_URL}`, {
        method: 'POST',
        body: JSON.stringify(template)
    }),
    update: (id, template) => apiFetch(`${METAL_EMAIL_TEMPLATES_API_URL}/${id}`, {
        method: 'PUT',
        body: JSON.stringify(template)
    }),
    delete: (id) => apiFetch(`${METAL_EMAIL_TEMPLATES_API_URL}/${id}`, {
        method: 'DELETE'
    })
};