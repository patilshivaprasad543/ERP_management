const API = 'http://localhost:8080/api';

async function json(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) throw new Error(`API ${response.status}`);
  return response.json();
}

async function loadDashboard() {
  try {
    const [employees, departments] = await Promise.all([
      json(`${API}/employees`),
      json(`${API}/departments`)
    ]);
    document.querySelector('#employeeCount').textContent = employees.length;
    document.querySelector('#departmentCount').textContent = departments.length;
    renderEmployees(employees);
  } catch (error) {
    document.querySelector('#employeeTable').textContent = `Backend unavailable: ${error.message}`;
  }
}

function renderEmployees(items) {
  if (!items.length) {
    document.querySelector('#employeeTable').textContent = 'No employees found.';
    return;
  }
  const rows = items.map(e => `<tr><td>${e.employeeCode}</td><td>${e.firstName} ${e.lastName}</td><td>${e.email}</td><td>${e.status}</td></tr>`).join('');
  document.querySelector('#employeeTable').innerHTML = `<table class="table"><thead><tr><th>Code</th><th>Name</th><th>Email</th><th>Status</th></tr></thead><tbody>${rows}</tbody></table>`;
}

document.querySelector('#loadEmployees').addEventListener('click', loadDashboard);
document.querySelector('#refresh').addEventListener('click', loadDashboard);
loadDashboard();
