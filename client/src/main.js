import './style.css';
import page from 'page';
import { html, render } from 'lit';
import './views/metal-email-template/metal-email-template.js';

const outlet = document.getElementById('outlet');

page('/', () => {
  render(html`<metal-email-template></metal-email-template>`, outlet);
});

page('*', () => {
  page.redirect('/');
});

page.start();

console.log('Router initialized');
