(ns server.db
  (:require    [next.jdbc :as jdbc]
               [next.jdbc.sql :as sql]
               [next.jdbc.result-set :as rs])
  (:gen-class))

(def db-spec {:dbtype "postgresql"
              :dbname "testdb"
              :host "127.0.0.1"
              :port 5432
              :user "eurv"
              :password "pgpwd"})

(def ds (jdbc/get-datasource db-spec))

(defn query-database
  [sql-statement]
  (with-open [connection (jdbc/get-connection ds)]
    (sql/query connection [sql-statement] jdbc/snake-kebab-opts)))

(defn query-by-key
  ([table data] (query-by-key table data nil))
  ([table data opts]
   (with-open [connection (jdbc/get-connection ds)]
     (sql/find-by-keys
      connection
      table
      data
      (merge {:column-fn (:column-fn jdbc/snake-kebab-opts)
              :builder-fn rs/as-unqualified-kebab-maps} opts)))))

(defn query-by-id
  ([table id] (query-by-id table id nil))
  ([table id opts]
   (with-open [connection (jdbc/get-connection ds)]
     (sql/get-by-id
      connection
      table
      id
      (merge {:column-fn (:column-fn jdbc/snake-kebab-opts)
              :builder-fn rs/as-unqualified-kebab-maps} opts)))))

(defn insert-data
  [table record-data]
  (with-open [connection (jdbc/get-connection ds)]
    (sql/insert! connection table record-data jdbc/unqualified-snake-kebab-opts)))

(defn update-data
  [table record-data where-params]
  (with-open [connection (jdbc/get-connection ds)]
    (sql/update! connection table record-data where-params jdbc/unqualified-snake-kebab-opts)))

(defn delete-by-key
  [table key value]
  (with-open [connection (jdbc/get-connection ds)]
    (sql/delete! connection table {key value})))