- Удаление триггера и функции
DROP TRIGGER IF EXISTS statuses_updated_at_trigger ON statuses;
DROP FUNCTION IF EXISTS update_statuses_updated_at;

-- Удаление таблицы statuses
DROP TABLE statuses;