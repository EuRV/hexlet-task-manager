(ns server.db.core
  (:require    [next.jdbc :as jdbc]
               [migratus.core :as migratus]
               [clojure.java.io :as io]
               [clojure.edn :as edn])
  (:gen-class))

(def config
  (-> "config.edn"
      io/resource
      slurp
      edn/read-string))

(def db-spec (:db config))

(def ds (jdbc/get-datasource db-spec))

(def migratus-config
  (merge (:migratus config)
         {:db (:db config)}))

(defn migrate []
  (migratus/migrate migratus-config))

(defn rollback []
  (migratus/rollback migratus-config))

(defn -main [args]
  (case args
    "migrate" (migrate)
    "rollback" (rollback)
    (println "Usage: clj -m migrate [migrate | rollback]")))