(ns server.models.tasks
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as string]
   [server.db.sql.queries :as db])
  (:gen-class))

(defn at-least [n]
  (fn [data]
    (>= (count data) n)))

(s/def ::at-least-one
  (at-least 1))

(s/def ::string string?)

(s/def ::number number?)

(s/def ::empty #(not (nil? %)))

(s/def :tasks/name
  (s/and ::string ::at-least-one))

(s/def :tasks/status-id
  (s/and ::empty ::number))

(s/def :tasks/creator-id
  (s/and ::empty ::number))

(s/def :tasks/description ::string)

(s/def :tasks/executor-id ::number)

(s/def :tasks/entity
  (s/keys :req-un [:tasks/name
                   :tasks/status-id
                   :tasks/creator-id]
          :opt-un [:tasks/description
                   :tasks/executor-id]))

(def spec-errors
  {::at-least-one "Должно быть не менее одного символа"
   ::string "Должна быть строкой"
   ::number "Должен быть числовой формат"
   ::empty "Должно быть не менее одного статуса"})

(defn validate-task
  [task]
  (if (s/valid? :tasks/entity task)
    {:valid? true :errors {} :values task}
    {:valid? false
     :errors (->> (s/explain-data :tasks/entity task)
                  :clojure.spec.alpha/problems
                  (reduce (fn
                            [init problem]
                            (assoc init (-> problem :in last) (get spec-errors (-> problem :via peek))))
                          {}))
     :values (->> (s/explain-data :tasks/entity task)
                  :clojure.spec.alpha/value)}))

(defn pgarray-to-vector [pgarray]
  (if (and (some? pgarray) (instance? org.postgresql.jdbc.PgArray pgarray))
    (vec (.getArray pgarray))
    pgarray))

(defn create-task
  [task]
  (db/insert-data :tasks task))

(defn get-tasks
  [& {:keys [creator-id executor-id status-id]}]
  (let [base-query "
      SELECT 
        t.id,
        t.name AS task_name,
        s.name AS status_name,
        CONCAT (uc.first_name, ' ', uc.last_name) AS creator_name,
        CONCAT (ue.first_name, ' ', ue.last_name) AS executor_name,
        TO_CHAR(t.created_at, 'FMMM/FMDD/YYYY, HH12:MI:SS AM') AS created_at
      FROM 
        tasks t
        JOIN statuses s ON t.status_id = s.id
        JOIN users uc ON t.creator_id = uc.id
        LEFT JOIN users ue ON t.executor_id = ue.id"
        where-clauses (->> [(when creator-id "t.creator_id = ?")
                            (when executor-id "t.executor_id = ?")
                            (when status-id "t.status_id = ?")]
                           (filter identity)
                           (string/join " AND "))
        query (if (empty? where-clauses)
                (str base-query " ORDER BY id ASC")
                (str base-query " WHERE " where-clauses " ORDER BY id ASC"))
        params (->> [creator-id executor-id status-id]
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
  [id values]
  (db/update-data :tasks values {:id id}))

(defn delete-task
  [id]
  (db/delete-by-key :tasks :id id))

(comment
  (defn get-tasks-with-filters [{:keys [status-id creator-id executor-id label-id]}]
    (let [base-query "
        SELECT
            t.id,
            t.name,
            t.description,
            s.name AS status_name,
            creator.first_name AS creator_name,
            executor.first_name AS executor_name
        FROM 
            tasks t
        JOIN 
            statuses s ON t.status_id = s.id
        JOIN 
            users creator ON t.creator_id = creator.id
        JOIN 
            users executor ON t.executor_id = executor.id"
          where-clause "
        WHERE 
            (? IS NULL OR t.status_id = ?)
            AND (? IS NULL OR t.creator_id = ?)
            AND (? IS NULL OR t.executor_id = ?)
            AND (? IS NULL OR EXISTS (
                SELECT 1 
                FROM labels_tasks lt 
                WHERE lt.task_id = t.id 
                AND lt.label_id = ?
            ))"
          query (str base-query where-clause)
          params {:status_id status-id
                  :creator_id creator-id
                  :executor_id executor-id
                  :labels_id label-id}
          result (db/query-tasks [query params])]
      result))

  (get-tasks-with-filters {:status-id 2 :label-id 1})
  :rcf)