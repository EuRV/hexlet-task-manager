(ns server.db
  (:require [next.jdbc :as jdbc]))

(def db {:dbtype "postgresql"
         :dbname "testdb"
         :host "127.0.0.1"
         :port 5432
         :user "eurv"
         :password "pgpwd"})

(def ds (jdbc/get-datasource db))

(comment

  :rcf)