(ns server.models.tasks
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [server.db.sql.queries :as db]
   [server.helpers :refer [clean-task-data validate-data]])
  (:gen-class))

(defn at-least [n]
  (fn [data]
    (>= (count data) n)))

(s/def ::at-least-one
  (at-least 1))

(s/def ::string string?)

(s/def ::number number?)

(s/def ::vector (s/and coll? vector?))

(s/def ::numeric-items (s/coll-of number? :kind vector?))

(s/def ::empty #(not (nil? %)))

(s/def :tasks/name
  (s/and ::string ::at-least-one))

(s/def :tasks/status-id
  (s/and ::empty ::number))

(s/def :tasks/creator-id
  (s/and ::empty ::number))

(s/def :tasks/description ::string)

(s/def :tasks/executor-id ::number)

(s/def :tasks/labels
  (s/and ::vector ::numeric-items))

(s/def :tasks/entity
  (s/keys :req-un [:tasks/name
                   :tasks/status-id
                   :tasks/creator-id]
          :opt-un [:tasks/description
                   :tasks/executor-id
                   :tasks/labels]))

(def spec-errors
  {::at-least-one "Должно быть не менее одного символа"
   ::string "Должна быть строкой"
   ::number "Должен быть числовой формат"
   ::empty "Должно быть не менее одного статуса"
   ::vector "Должен быть вектор"
   ::numeric-items "Вектор содержит нечисловые элементы"})

(def validate-task
  (validate-data :tasks/entity spec-errors))

(defn pgarray-to-vector [pgarray]
  (if (and (some? pgarray) (instance? org.postgresql.jdbc.PgArray pgarray))
    (vec (.getArray pgarray))
    pgarray))

(defn create-task
  [data]
  (let [validated-data (-> data
                        clean-task-data
                        validate-task)]
    (if (:valid? validated-data)
      (try
        (let [task (dissoc (:values validated-data) :labels)
              labels (:labels (:values validated-data))]
          (db/create-task-with-labels task labels))
        (catch Exception _
          (-> validated-data
              (dissoc :valid?)
              (assoc-in [:errors] {:name "Какая-то ошибка базы данных"}))))
      (-> validated-data
          (dissoc :valid?)))))

(defn get-tasks
  [& {:keys [status-id creator-id executor-id label-id]}]
  (let [base-query "SELECT DISTINCT
                      t.id,
                      t.name AS task_name,
                      s.name AS status_name,
                      CONCAT (uc.first_name, ' ', uc.last_name) AS creator_name,
                      CONCAT (ue.first_name, ' ', ue.last_name) AS executor_name,
                      TO_CHAR(t.created_at, 'FMMM/FMDD/YYYY, HH12:MI:SS AM') AS created_at
                    FROM tasks t
                    JOIN statuses s ON t.status_id = s.id
                    JOIN users uc ON t.creator_id = uc.id
                    LEFT JOIN users ue ON t.executor_id = ue.id
                    LEFT JOIN labels_tasks lt ON t.id = lt.task_id
                    LEFT JOIN labels l ON lt.label_id = l.id"
        where-clauses (->> [(when creator-id "t.creator_id = ?")
                            (when executor-id "t.executor_id = ?")
                            (when status-id "t.status_id = ?")
                            (when label-id "lt.label_id = ?")]
                           (filter identity)
                           (string/join " AND "))
        query (if (empty? where-clauses)
                (str base-query " ORDER BY id ASC")
                (str base-query " WHERE " where-clauses " ORDER BY id ASC"))
        params (->> [creator-id executor-id status-id label-id]
                    (filter identity))]
    (db/query-database (into [query] params))))

(defn get-task-relation
  [id]
  (->>
   (db/query-database ["SELECT
                          t.id,
                          COALESCE(t.description, ' ') AS description,
                          t.name AS task_name,
                          s.name AS status_name,
                          CONCAT (c.first_name, ' ', c.last_name) AS creator_name,
                          CONCAT (e.first_name, ' ', e.last_name) AS executor_name,
                          array_remove(array_agg(l.name), NULL) AS labels,
                          TO_CHAR(t.created_at, 'FMMM/FMDD/YYYY, HH12:MI:SS AM') AS created_at
                        FROM tasks t
                        JOIN statuses s ON t.status_id = s.id
                        JOIN users c ON t.creator_id = c.id
                        LEFT JOIN users e ON t.executor_id = e.id
                        LEFT JOIN labels_tasks lt ON t.id = lt.task_id
                        LEFT JOIN labels l ON lt.label_id = l.id
                        WHERE t.id = ?
                        GROUP BY t.id, t.name, s.name, creator_name, executor_name" id])
   (mapv #(update % :labels pgarray-to-vector))))

(defn get-task
  [id]
  (db/query-by-id
   :tasks
   id
   {:columns [:id :name :description :status-id :creator-id :executor-id]}))

(defn update-task
  [data id]
  (let [validated-data (-> data
                        clean-task-data
                        validate-task)]
    (if (:valid? validated-data)
      (try
        (let [task (dissoc (:values validated-data) :labels)
              labels (:labels (:values validated-data))]
          (db/update-task-with-labels id task labels))
        (catch Exception _
          (-> validated-data
              (dissoc :valid?)
              (assoc-in [:errors] {:name "Какая-то ошибка базы данных"}))))
      (-> validated-data
          (dissoc :valid?)))))

(defn delete-task
  [id]
  (db/delete-by-key :tasks :id id))