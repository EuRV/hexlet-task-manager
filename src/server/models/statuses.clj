(ns server.models.statuses
  (:require [clojure.spec.alpha :as s]
  
            [server.db :as db])
  (:gen-class))

(defn at-least [n]
  (fn [data]
    (>= (count data) n)))

(s/def ::at-least-one
  (at-least 1))

(s/def ::string string?)

(s/def :statuses/name
  (s/and ::string ::at-least-one))

(s/def :statuses/entity
  (s/keys :req-un [:statuses/name]))

(defn get-statuses
  []
  (db/query-database "SELECT id, name, created_at FROM statuses ORDER BY id ASC"))