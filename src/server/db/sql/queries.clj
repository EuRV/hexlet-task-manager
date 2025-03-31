(ns server.db.sql.queries
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as sql]
   [next.jdbc.result-set :as rs]

   [server.db.core :refer [ds]])
  (:gen-class))

(defn query-database
  [sql-statement]
  (try
    (with-open [connection (jdbc/get-connection ds)]
      (sql/query
       connection
       sql-statement
       {:builder-fn rs/as-unqualified-kebab-maps}))
    (catch Exception e
      (throw e))))

(defn query-by-key
  ([table data] (query-by-key table data nil))
  ([table data opts]
   (try
     (with-open [connection (jdbc/get-connection ds)]
       (sql/find-by-keys
        connection
        table
        data
        (merge {:column-fn (:column-fn jdbc/snake-kebab-opts)
                :builder-fn rs/as-unqualified-kebab-maps} opts)))
     (catch Exception e
       (throw e)))))

(defn query-by-id
  ([table id] (query-by-id table id nil))
  ([table id opts]
   (try
     (with-open [connection (jdbc/get-connection ds)]
       (sql/get-by-id
        connection
        table
        id
        (merge {:column-fn (:column-fn jdbc/snake-kebab-opts)
                :builder-fn rs/as-unqualified-kebab-maps} opts)))
     (catch Exception e
       (throw e)))))

(defn insert-data
  [record-data table]
  (try
    (with-open [connection (jdbc/get-connection ds)]
      (sql/insert! connection table record-data jdbc/unqualified-snake-kebab-opts))
    (catch Exception e
      (throw e))))

(defn update-data
  [record-data where-params table]
  (try
    (with-open [connection (jdbc/get-connection ds)]
      (sql/update! connection table record-data where-params jdbc/unqualified-snake-kebab-opts))
    (catch Exception e
      (throw e))))

(defn delete-by-key
  [table key value]
  (try
    (with-open [connection (jdbc/get-connection ds)]
      (sql/delete! connection table {key value}))
    (catch Exception e
      (throw e))))

(defn create-task-with-labels [task-data label-ids]
  (jdbc/with-transaction [tx ds]
    (try
      (let [{:keys [id]} (sql/insert! tx :tasks task-data jdbc/unqualified-snake-kebab-opts)]
        (doseq [label-id label-ids]
          (sql/insert! tx :labels_tasks {:label_id label-id :task_id id} jdbc/unqualified-snake-kebab-opts)))
      (catch Exception e
        (throw (ex-info "Ошибка при создании задачи" {:cause (.getMessage e)}))))))

(defn update-task-with-labels [task-id updated-task-data label-ids]
  (jdbc/with-transaction [tx ds]
    (try
      (sql/update! tx :tasks updated-task-data ["id = ?" task-id] jdbc/unqualified-snake-kebab-opts)
      (sql/delete! tx :labels_tasks ["task_id = ?" task-id] jdbc/unqualified-snake-kebab-opts)
      (doseq [label-id label-ids]
        (sql/insert! tx :labels_tasks {:task_id task-id :label_id label-id} jdbc/unqualified-snake-kebab-opts))
      (catch Exception e
        (throw (ex-info "Ошибка при обновлении задачи" {:cause (.getMessage e)}))))))

(defn delete-task-with-labels [task-id]
  (jdbc/with-transaction [tx ds]
    (try
      (sql/delete! tx :labels_tasks ["task_id = ?" task-id])
      (sql/delete! tx :tasks ["id = ?" task-id])
      (catch Exception e
        (throw (ex-info "Ошибка при удалении задачи" {:cause (.getMessage e)}))))))