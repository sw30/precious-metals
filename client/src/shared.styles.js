import { css } from 'lit';

export const buttonStyles = css`
  button {
    border-radius: 4px;
    border: 1px solid #ddd;
    padding: 0.6em 1.2em;
    font-size: 0.95rem;
    font-weight: 500;
    font-family: inherit;
    background-color: #ffffff;
    color: #333;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  button:hover {
    border-color: #aaa;
    background-color: #f8f8f8;
  }

  button:focus-visible {
    outline: 2px solid #111;
    outline-offset: 2px;
  }

  .btn-primary {
    background: #111;
    color: white !important;
    border: none;
  }

  .btn-primary:hover {
    background: #333;
  }

  .btn-secondary {
    background: white;
    color: #666;
    border: 1px solid #eee;
  }

  .btn-secondary:hover {
    background: #f8f8f8;
    border-color: #ccc;
  }

  .btn-danger {
    color: #d32f2f;
    border-color: #ffcdd2;
  }

  .btn-danger:hover {
    background: #fff8f8;
  }

  .btn-remove {
    color: #999;
    border: none;
    background: none;
    padding: 0.2rem 0.5rem;
    font-size: 0.85rem;
  }

  .btn-remove:hover {
    color: #d32f2f;
    background: #fff0f0;
  }
`;

export const formStyles = css`
  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.4rem;
  }

  label {
    font-weight: 500;
    font-size: 0.9rem;
    color: #555;
  }

  input, select, textarea {
    padding: 0.6rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-family: inherit;
    font-size: 0.9rem;
    background: white;
    transition: border-color 0.2s;
  }

  input:focus, select:focus, textarea:focus {
    outline: none;
    border-color: #111;
  }

  textarea {
    padding: 0.75rem;
    font-size: 1rem;
  }
`;

export const headerStyles = css`
  h1, h2, h3, h4 {
    color: #111;
    font-weight: 600;
    margin-top: 0;
  }
`;
