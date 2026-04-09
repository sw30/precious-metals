import {BackendApiError} from "../model/ApiError.js";

const API_BASE_URL = '/api/v1'
/*
* For demo purposes, production will use BFF
* */
const ADMIN_API_KEY = 'admin-key-67890';

export async function apiFetch(endpoint, options = {}) {
    const defaultHeaders = {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'X-API-KEY': ADMIN_API_KEY,
    };

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers
        },
        credentials: 'include'
    });

    if (!response.ok) {
        const errorPayload = await response.json().catch(() => ({
            message: `API HTTP Error: ${response.status}`,
            errorCode: 'HTTP_ERROR'
        }));

        throw new BackendApiError(errorPayload);
    }

    if (response.status === 204) return null;

    return response.json();
}