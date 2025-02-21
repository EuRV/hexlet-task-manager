(ns server.models.tasks
  (:require [clojure.spec.alpha :as s]
  
            [server.db.sql.queries :as db]
            [server.helpers :refer [to-number]])
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



(defn create-tasks
  [tasks]
  (db/insert-data :tasks tasks))

(defn get-tasks
  []
  (db/query-database "SELECT
                        t.id,
                        t.name AS task_name,
                        s.name AS status_name,
                        CONCAT (c.first_name, ' ', c.last_name) AS creator_name,
                        CONCAT (e.first_name, ' ', e.last_name) AS executor_name,
                        t.created_at
                      FROM tasks t
                      JOIN statuses s ON t.status_id = s.id
                      JOIN users c ON t.creator_id = c.id
                      JOIN users e ON t.executor_id = e.id
                      ORDER BY id ASC"))

(comment
  (create-tasks {:name "go"
                 :description "go to the store"
                 :status_id 1
                 :creator_id 1
                 :executor_id 2})
  
  (get-tasks)

  (defn to-number [value]
    (cond
      (number? value) value
      (string? value) (try
                        (Long/parseLong value)
                        (catch Exception _
                          nil))))
  
  (to-number "125341")
  :rcf)