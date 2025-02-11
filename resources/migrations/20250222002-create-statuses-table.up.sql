-- Создание таблицы statuses
CREATE TABLE statuses (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Создание функции для обновления updated_at
CREATE OR REPLACE FUNCTION update_statuses_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание триггера для автоматического обновления updated_at
CREATE TRIGGER statuses_updated_at_trigger
BEFORE UPDATE ON statuses
FOR EACH ROW
EXECUTE FUNCTION update_statuses_updated_at();
