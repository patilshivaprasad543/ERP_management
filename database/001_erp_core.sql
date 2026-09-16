CREATE TABLE IF NOT EXISTS departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS employees (
    id BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(30) NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    phone VARCHAR(25),
    joining_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    department_id BIGINT NOT NULL REFERENCES departments(id)
);

-- Planned enterprise modules: attendance, leave_requests, expenses, vendors,
-- purchase_orders, products, inventory_transactions, invoices, payments,
-- notifications and audit_logs. They will be introduced through versioned migrations.
