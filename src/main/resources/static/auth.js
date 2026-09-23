const csrf = () => document.cookie.split('; ').find(row => row.startsWith('XSRF-TOKEN='))?.split('=')[1];
async function submitAuth(event, endpoint, redirect) {
  event.preventDefault();
  const form = event.target, message = document.querySelector('#message');
  message.textContent = 'Please wait…'; message.className = 'message';
  const data = Object.fromEntries(new FormData(form));
  const isLogin = endpoint.endsWith('/login');
  const response = await fetch(endpoint, {method:'POST', headers:{...(isLogin ? {'Content-Type':'application/x-www-form-urlencoded'} : {'Content-Type':'application/json'}),'X-XSRF-TOKEN':decodeURIComponent(csrf() || '')}, body:isLogin ? new URLSearchParams(data) : JSON.stringify(data)});
  const body = await response.json().catch(() => ({}));
  if (!response.ok) { message.textContent = body.message || 'Please check the form and try again.'; return; }
  if (redirect) { window.location.href = redirect; return; }
  message.textContent = body.message; message.className = 'message success'; form.reset();
}
async function loadDashboard() {
  const response = await fetch('/api/me');
  if (!response.ok) { window.location.href='/login.html'; return; }
  const user = await response.json();
  document.querySelector('#user-name').textContent = user.name;
  document.querySelector('#user-email').textContent = user.email;
}
async function logout() {
  await fetch('/api/auth/logout',{method:'POST',headers:{'X-XSRF-TOKEN':decodeURIComponent(csrf() || '')}});
  window.location.href='/login.html';
}
