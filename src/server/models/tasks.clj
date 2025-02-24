(ns server.models.tasks
  (:require [clojure.spec.alpha :as s]
  
            [server.db.sql.queries :as db])
  (:gen-class))

(defn at-least [n]
  (fn [data]
    (>= (count data) n)))

()

(s/def ::at-least-one
  (at-least 1))

(s/def ::string string?)

(s/def ::number number?)

(s/def ::empty #(not (empty? %)))

(s/def :tasks/name
  (s/and ::string ::at-least-one))



(defn create-task
  [task]
  (db/insert-data :tasks task))

(defn get-tasks
  []
  (db/query-database ["SELECT
                        t.id,
                        t.name AS task_name,
                        s.name AS status_name,
                        CONCAT (c.first_name, ' ', c.last_name) AS creator_name,
                        CONCAT (e.first_name, ' ', e.last_name) AS executor_name,
                        TO_CHAR(t.created_at, 'FMMM/FMDD/YYYY, HH12:MI:SS AM') AS created_at
                      FROM tasks t
                      JOIN statuses s ON t.status_id = s.id
                      JOIN users c ON t.creator_id = c.id
                      JOIN users e ON t.executor_id = e.id
                      ORDER BY id ASC"]))

(defn get-task
  [id]
  (db/query-database ["SELECT
                        t.id,
                        t.description,
                        t.name AS task_name,
                        s.name AS status_name,
                        CONCAT (c.first_name, ' ', c.last_name) AS creator_name,
                        CONCAT (e.first_name, ' ', e.last_name) AS executor_name,
                        TO_CHAR(t.created_at, 'FMMM/FMDD/YYYY, HH12:MI:SS AM') AS created_at
                      FROM tasks t
                      JOIN statuses s ON t.status_id = s.id
                      JOIN users c ON t.creator_id = c.id
                      JOIN users e ON t.executor_id = e.id
                      WHERE t.id = ?" id]))