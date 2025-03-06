(ns server.models.labels
  (:require
   [clojure.spec.alpha :as s]

   [server.db.sql.queries :as db])
  (:gen-class))

(defn at-least [n]
  (fn [data]
    (>= (count data) n)))

(s/def ::at-least-one
  (at-least 1))

(s/def ::string string?)

(s/def :labels/name
  (s/and ::string ::at-least-one))

(s/def :labels/entity
  (s/keys :req-un [:labels/name]))

(def spec-errors
  {::at-least-one "Должно быть не менее одного символа"
   ::string "Должна быть строкой"})

(defn validate-label
  "Validates a labels map and returns a map with :valid? and :errors keys."
  [labels]
  (if (s/valid? :labels/entity labels)
    {:valid? true :errors {} :values labels}
    {:valid? false
     :errors (->> (s/explain-data :labels/entity labels)
                  :clojure.spec.alpha/problems
                  (reduce (fn
                            [init problem]
                            (assoc init (-> problem :in last) (get spec-errors (-> problem :via peek))))
                          {}))
     :values (->> (s/explain-data :labels/entity labels)
                  :clojure.spec.alpha/value)}))

(defn create-label
  [label]
  (db/insert-data :labels label))

(defn get-labels
  []
  (db/query-database ["SELECT
                         id,
                         name,
                         TO_CHAR(created_at, 'FMMM/FMDD/YYYY, FMHH12:MI:SS AM') AS created_at
                       FROM labels
                       ORDER BY id ASC"]))

(defn get-label
  [id]
  (db/query-by-id :labels id))

(defn update-label
  [id values]
  (db/update-data :labels values {:id id}))

(defn delete-label
  [id]
  (db/delete-by-key :labels :id id))