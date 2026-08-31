-- Corrige a credencial administrativa criada pela V1.
-- Senha de desenvolvimento correspondente ao BCrypt: Admin@123

UPDATE users
SET password = '$2a$12$Urz50B.CBfVMyhN2U2uiz.tyeay5SeushypvqGx/9SncQs5AhiZO2',
    password_changed_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
WHERE username = 'admin';

-- Garante que o usuário administrativo esteja ativo.
UPDATE users
SET active = TRUE,
    email_verified = TRUE,
    updated_at = CURRENT_TIMESTAMP
WHERE username = 'admin';

-- Garante a associação do usuário admin à ROLE_ADMIN.
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.username = 'admin'
    ON CONFLICT (user_id, role_id) DO NOTHING;