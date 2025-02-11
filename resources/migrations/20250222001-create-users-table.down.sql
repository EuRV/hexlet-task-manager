-- Удаление триггера и функции
DROP TRIGGER IF EXISTS users_updated_at_trigger ON users;
DROP FUNCTION IF EXISTS update_users_updated_at;

-- Удаление таблицы users
DROP TABLE users;