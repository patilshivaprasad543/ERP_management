const API = 'http://localhost:8080/api';
const tokenKey = 'erp_access_token';
const sessionKey = 'erp_session';

function token() { return localStorage.getItem(tokenKey); }
function session() {
  try { return JSON.parse(localStorage.getItem(sessionKey) || 'null'); } catch (_) { return null; }
}
function isFinanceUser() { return ['SUPER_ADMIN', 'ADMIN', 'FINANCE'].includes(session()?.role); }
function isProcurementUser() { return ['SUPER_ADMIN', 'ADMIN', 'PROCUREMENT'].includes(session()?.role); }

function showApp(currentSession) {
  document.querySelector('#loginView').classList.add('hidden');
  document.querySelector('#appView').classList.remove('hidden');
  document.querySelector('#sessionUser').textContent = `${currentSession.username} · ${currentSession.role}`;
  document.querySelector('#finance').classList.toggle('hidden', !isFinanceUser());
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
  if (response.status === 401) {
    localStorage.removeItem(tokenKey); localStorage.removeItem(sessionKey); showLogin();
    throw new Error('Session expired');
  }
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
  const username = document.querySelector('#username').value.trim();
  const password = document.querySelector('#password').value;
  message.textContent = 'Signing in…';
  try {
    const result = await json(`${API}/auth/login`, {method:'POST', body:JSON.stringify({username, password})});
    localStorage.setItem(tokenKey, result.accessToken);
    localStorage.setItem(sessionKey, JSON.stringify(result));
    showApp(result);
  } catch (error) { message.textContent = error.message; }
}

async function loadDashboard() {
  const requests = [
    json(`${API}/employees`),
    json(`${API}/departments`),
    json(`${API}/inventory`)
  ];
  if (isProcurementUser()) requests.push(json(`${API}/procurement/purchase-requests/status/SUBMITTED`));
  if (isFinanceUser()) requests.push(json(`${API}/finance/payables/status/OPEN`));

  const results = await Promise.allSettled(requests);
  const employees = results[0].status === 'fulfilled' ? results[0].value : [];
  const departments = results[1].status === 'fulfilled' ? results[1].value : [];
  const inventory = results[2].status === 'fulfilled' ? results[2].value : [];
  const pendingIndex = isProcurementUser() ? 3 : -1;
  const payableIndex = isProcurementUser() ? 4 : 3;
  const pending = pendingIndex >= 0 && results[pendingIndex]?.status === 'fulfilled' ? results[pendingIndex].value : [];
  const openPayables = isFinanceUser() && results[payableIndex]?.status === 'fulfilled' ? results[payableIndex].value : [];

  document.querySelector('#employeeCount').textContent = employees.length;
  document.querySelector('#departmentCount').textContent = departments.length;
  document.querySelector('#inventoryCount').textContent = inventory.length;
  document.querySelector('#pendingRequests').textContent = isProcurementUser() ? pending.length : '—';
  document.querySelector('#openPayables').textContent = isFinanceUser() ? openPayables.length : '—';
  renderEmployees(employees);
  if (isProcurementUser()) renderRequests(pending);
  renderInventory(inventory);
  if (isFinanceUser()) renderPayables(openPayables);
}

function renderEmployees(items) {
  if (!items.length) { document.querySelector('#employeeTable').textContent = 'No employees found.'; return; }
  const rows = items.map(e => `<tr><td>${escapeHtml(e.employeeCode)}</td><td>${escapeHtml(`${e.firstName} ${e.lastName}`)}</td><td>${escapeHtml(e.email)}</td><td>${escapeHtml(e.status)}</td></tr>`).join('');
  document.querySelector('#employeeTable').innerHTML = `<table class="table"><thead><tr><th>Code</th><th>Name</th><th>Email</th><th>Status</th></tr></thead><tbody>${rows}</tbody></table>`;
}
function renderRequests(items) {
  const el = document.querySelector('#procurementTable');
  if (!items.length) { el.textContent = 'No submitted purchase requests.'; return; }
  el.innerHTML = `<table class="table"><thead><tr><th>ID</th><th>Employee</th><th>Vendor</th><th>Item</th><th>Qty</th><th>Unit Price</th><th>Status</th></tr></thead><tbody>${items.map(r => `<tr><td>#${r.id}</td><td>${r.requesterEmployeeId}</td><td>${r.vendorId}</td><td>${escapeHtml(r.itemDescription)}</td><td>${r.quantity}</td><td>${r.estimatedUnitPrice}</td><td><span class="status">${escapeHtml(r.status)}</span></td></tr>`).join('')}</tbody></table>`;
}
function renderInventory(items) {
  const el = document.querySelector('#inventoryTable');
  if (!items.length) { el.textContent = 'No inventory items found.'; return; }
  el.innerHTML = `<table class="table"><thead><tr><th>SKU</th><th>Name</th><th>Unit</th><th>On hand</th><th>Unit cost</th><th>Status</th></tr></thead><tbody>${items.map(i => `<tr><td>${escapeHtml(i.sku)}</td><td>${escapeHtml(i.name)}</td><td>${escapeHtml(i.unit || '')}</td><td>${i.quantityOnHand}</td><td>${i.unitCost}</td><td>${escapeHtml(i.status)}</td></tr>`).join('')}</tbody></table>`;
}
function renderPayables(items) {
  const el = document.querySelector('#payablesTable');
  if (!items.length) { el.textContent = 'No open vendor payables.'; return; }
  el.innerHTML = `<table class="table"><thead><tr><th>ID</th><th>PO</th><th>Vendor</th><th>Amount</th><th>Due</th><th>Status</th></tr></thead><tbody>${items.map(p => `<tr><td>#${p.id}</td><td>#${p.purchaseOrderId}</td><td>${p.vendorId}</td><td>${p.amount}</td><td>${p.dueDate || '—'}</td><td><span class="status">${escapeHtml(p.status)}</span></td></tr>`).join('')}</tbody></table>`;
}
function escapeHtml(value) { return String(value ?? '').replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','\"':'&quot;'}[c])); }

document.querySelector('#loginForm').addEventListener('submit', login);
document.querySelector('#loadEmployees').addEventListener('click', loadDashboard);
document.querySelector('#refreshProcurement').addEventListener('click', loadDashboard);
document.querySelector('#refreshInventory').addEventListener('click', loadDashboard);
document.querySelector('#refreshFinance').addEventListener('click', loadDashboard);
document.querySelector('#logout').addEventListener('click', () => { localStorage.removeItem(tokenKey); localStorage.removeItem(sessionKey); showLogin(); });

const savedSession = session();
if (token() && savedSession) showApp(savedSession); else showLogin();
