(ns server.db
  (:require    [next.jdbc :as jdbc]
               [next.jdbc.sql :as sql])
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

(defn insert-data
  [table record-data]
  (with-open [connection (jdbc/get-connection ds)]
    (sql/insert! connection table record-data jdbc/unqualified-snake-kebab-opts)))