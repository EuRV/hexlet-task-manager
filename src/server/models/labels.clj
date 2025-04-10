(ns server.models.labels
  (:require
   [clojure.spec.alpha :as s]

   [server.db.sql.queries :as db]
   [server.helpers :refer [validate-data]])
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

(def validate-label
  (validate-data :labels/entity spec-errors))

(defn create-label
  [data]
  (let [label (validate-label data)]
    (if (:valid? label)
      (try
        (-> (:values label)
            (db/insert-data :labels))
        (catch org.postgresql.util.PSQLException e
          (let [sql-state (.getSQLState e)]
            (if (= sql-state "23505")
              (-> label
                  (dissoc :valid?)
                  (assoc-in [:errors] {:name "Такая метка уже существует"}))
              (-> label
                  (dissoc :valid?)
                  (assoc-in [:errors] {:name "Какая-то ошибка базы данных"}))))))
      (-> label
          (dissoc :valid?)))))

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
  [data id]
  (let [label (validate-label data)]
    (if (:valid? label)
      (try
        (-> (:values label)
            (db/update-data :labels {:id id}))
        (catch org.postgresql.util.PSQLException e
          (let [sql-state (.getSQLState e)]
            (if (= sql-state "23505")
              (-> label
                  (dissoc :valid?)
                  (assoc-in [:errors] {:name "Такая метка уже существует"}))
              (-> label
                  (dissoc :valid?)
                  (assoc-in [:errors] {:name "Какая-то ошибка базы данных"}))))))
      (-> label
          (dissoc :valid?)))))

(defn delete-label
  [id]
  (db/delete-by-key :labels :id id))