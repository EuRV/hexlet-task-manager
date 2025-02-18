(ns server.models.statuses
  (:require [clojure.spec.alpha :as s]
  
            [server.db.sql.queries :as db])
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

(def spec-errors
  {::at-least-one "Должно быть не менее одного символа"
   ::string "Должна быть строкой"})

(defn validate-statuses
  "Validates a statuses map and returns a map with :valid? and :errors keys."
  [statuses]
  (if (s/valid? :statuses/entity statuses)
    {:valid? true :errors {} :values statuses}
    {:valid? false
     :errors (->> (s/explain-data :statuses/entity statuses)
                  :clojure.spec.alpha/problems
                  (reduce (fn
                            [init problem]
                            (assoc init (-> problem :in last) (get spec-errors (-> problem :via peek))))
                          {}))
     :values (->> (s/explain-data :statuses/entity statuses)
                  :clojure.spec.alpha/value)}))

(defn create-statuses
  [statuses]
  (db/insert-data :statuses statuses))

(defn get-statuses
  []
  (db/query-database "SELECT id, name, created_at FROM statuses ORDER BY id ASC"))

(defn get-status
  [id]
  (db/query-by-id :statuses id))

(defn update-status
  [id values]
  (db/update-data :statuses values {:id id}))