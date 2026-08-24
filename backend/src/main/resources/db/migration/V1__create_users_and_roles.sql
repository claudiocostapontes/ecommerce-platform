-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
                                     id UUID PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT
    );

-- Create users table
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP,
    password_changed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    version BIGINT
    );

-- Create user_roles junction table
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id UUID NOT NULL,
                                          role_id UUID NOT NULL,
                                          PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- Insert default roles
MERGE INTO roles (id, name, description, created_at) KEY (name)
VALUES (RANDOM_UUID(), 'ROLE_ADMIN', 'Administrator with full access', CURRENT_TIMESTAMP),
       (RANDOM_UUID(), 'ROLE_MANAGER', 'Manager with limited admin access', CURRENT_TIMESTAMP),
       (RANDOM_UUID(), 'ROLE_OPERATOR', 'Operator for order and inventory management', CURRENT_TIMESTAMP),
       (RANDOM_UUID(), 'ROLE_CUSTOMER', 'Customer role', CURRENT_TIMESTAMP);

-- Insert default admin user (password: admin123)
-- BCrypt hash for 'admin123' with strength 12
MERGE INTO users (id, username, email, password, first_name, last_name, active, email_verified, password_changed_at, created_at) KEY (username)
VALUES (RANDOM_UUID(),
        'admin',
        'admin@ecommerce.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg0pFmPZm5a',
        'Admin',
        'User',
        TRUE,
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- Assign admin role to admin user
MERGE INTO user_roles (user_id, role_id) KEY (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';