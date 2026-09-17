const API = 'http://localhost:8080/api';
const tokenKey = 'erp_access_token';
const sessionKey = 'erp_session';

function token() { return localStorage.getItem(tokenKey); }
function showApp(session) {
  document.querySelector('#loginView').classList.add('hidden');
  document.querySelector('#appView').classList.remove('hidden');
  document.querySelector('#sessionUser').textContent = `${session.username} · ${session.role}`;
  loadDashboard();
}
function showLogin() {
  document.querySelector('#loginView').classList.remove('hidden');
  document.querySelector('#appView').classList.add('hidden');
}

async function json(url, options = {}) {
  const headers = new Headers(options.headers || {});
  headers.set('Accept', 'application/json');
  if (options.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
  if (token()) headers.set('Authorization', `Bearer ${token()}`);
  const response = await fetch(url, {...options, headers});
  if (response.status === 401) { localStorage.removeItem(tokenKey); localStorage.removeItem(sessionKey); showLogin(); throw new Error('Session expired'); }
  if (!response.ok) {
    let message = `API ${response.status}`;
    try { const body = await response.json(); message = body.message || message; } catch (_) {}
    throw new Error(message);
  }
  return response.status === 204 ? null : response.json();
}

async function login(event) {
  event.preventDefault();
  const message = document.querySelector('#loginMessage');
  message.textContent = 'Signing in…';
  try {
    const result = await json(`${API}/auth/login`, {method:'POST', body:JSON.stringify({username:username.value.trim(), password:password.value})});
    localStorage.setItem(tokenKey, result.accessToken);
    localStorage.setItem(sessionKey, JSON.stringify(result));
    showApp(result);
  } catch (error) { message.textContent = error.message; }
}

async function loadDashboard() {
  try {
    const [employees, departments, inventory, pending] = await Promise.all([
      json(`${API}/employees`), json(`${API}/departments`), json(`${API}/inventory`), json(`${API}/procurement/purchase-requests/status/SUBMITTED`)
    ]);
    document.querySelector('#employeeCount').textContent = employees.length;
    document.querySelector('#departmentCount').textContent = departments.length;
    document.querySelector('#inventoryCount').textContent = inventory.length;
    document.querySelector('#pendingRequests').textContent = pending.length;
    renderEmployees(employees); renderRequests(pending); renderInventory(inventory);
  } catch (error) { document.querySelector('#employeeTable').textContent = `Backend unavailable: ${error.message}`; }
}

function renderEmployees(items) {
  if (!items.length) { document.querySelector('#employeeTable').textContent = 'No employees found.'; return; }
  const rows = items.map(e => `<tr><td>${e.employeeCode}</td><td>${e.firstName} ${e.lastName}</td><td>${e.email}</td><td>${e.status}</td></tr>`).join('');
  document.querySelector('#employeeTable').innerHTML = `<table class="table"><thead><tr><th>Code</th><th>Name</th><th>Email</th><th>Status</th></tr></thead><tbody>${rows}</tbody></table>`;
}
function renderRequests(items) {
  const el = document.querySelector('#procurementTable');
  if (!items.length) { el.textContent = 'No submitted purchase requests.'; return; }
  el.innerHTML = `<table class="table"><thead><tr><th>ID</th><th>Employee</th><th>Vendor</th><th>Item</th><th>Qty</th><th>Unit Price</th><th>Status</th></tr></thead><tbody>${items.map(r => `<tr><td>#${r.id}</td><td>${r.requesterEmployeeId}</td><td>${r.vendorId}</td><td>${escapeHtml(r.itemDescription)}</td><td>${r.quantity}</td><td>${r.estimatedUnitPrice}</td><td><span class="status">${r.status}</span></td></tr>`).join('')}</tbody></table>`;
}
function renderInventory(items) {
  const el = document.querySelector('#inventoryTable');
  if (!items.length) { el.textContent = 'No inventory items found.'; return; }
  el.innerHTML = `<table class="table"><thead><tr><th>SKU</th><th>Name</th><th>Unit</th><th>On hand</th><th>Unit cost</th><th>Status</th></tr></thead><tbody>${items.map(i => `<tr><td>${escapeHtml(i.sku)}</td><td>${escapeHtml(i.name)}</td><td>${escapeHtml(i.unit || '')}</td><td>${i.quantityOnHand}</td><td>${i.unitCost}</td><td>${i.status}</td></tr>`).join('')}</tbody></table>`;
}
function escapeHtml(value) { return String(value ?? '').replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','\"':'&quot;'}[c])); }

document.querySelector('#loginForm').addEventListener('submit', login);
document.querySelector('#loadEmployees').addEventListener('click', loadDashboard);
document.querySelector('#refreshProcurement').addEventListener('click', loadDashboard);
document.querySelector('#refreshInventory').addEventListener('click', loadDashboard);
document.querySelector('#logout').addEventListener('click', () => { localStorage.removeItem(tokenKey); localStorage.removeItem(sessionKey); showLogin(); });

const savedSession = localStorage.getItem(sessionKey);
if (token() && savedSession) { try { showApp(JSON.parse(savedSession)); } catch (_) { showLogin(); } } else showLogin();
