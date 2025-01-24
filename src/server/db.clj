(ns server.db
  (:require [hugsql.core :as hugsql]
            [hugsql.adapter.next-jdbc :as adapter]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs])
  (:gen-class))

(hugsql/def-db-fns "./../resources/sql/queries.sql"
  {:adapter (adapter/hugsql-adapter-next-jdbc
             {:builder-fn rs/as-kebab-maps})})

(def db-spec {:dbtype "postgresql"
         :dbname "testdb"
         :host "127.0.0.1"
         :port 5432
         :user "eurv"
         :password "pgpwd"})

(def ds (jdbc/get-datasource db-spec))

(defn get-users
  []
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (list-users ds))

(defn get-user
  [id]
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (get-user-by-id ds {:id id}))

(defn add-user
  [user]
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (create-user ds user))