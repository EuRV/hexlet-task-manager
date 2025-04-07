(ns server.models.statuses
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

(s/def :statuses/name
  (s/and ::string ::at-least-one))

(s/def :statuses/entity
  (s/keys :req-un [:statuses/name]))

(def spec-errors
  {::at-least-one "Должно быть не менее одного символа"
   ::string "Должна быть строкой"})

(def validate-status
  (validate-data :statuses/entity spec-errors))

(defn get-statuses
  []
  (db/query-database ["SELECT
                         id,
                         name,
                         TO_CHAR(created_at, 'FMMM/FMDD/YYYY, HH12:MI:SS AM') AS created_at
                       FROM statuses ORDER BY id ASC"]))

(defn get-status
  [id]
  (db/query-by-id :statuses id))

(defn create-statuses
  [data]
  (let [status (validate-status data)]
    (if (:valid? status)
      (try
        (-> (:values status)
            (db/insert-data :statuses))
        (catch org.postgresql.util.PSQLException e
          (let [sql-state (.getSQLState e)]
            (if (= sql-state "23505")
              (-> status
                  (dissoc :valid?)
                  (assoc-in [:errors] {:name "Такой статус уже существует"}))
              (-> status
                  (dissoc :valid?)
                  (assoc-in [:errors] {:name "Какая-то ошибка базы данных"}))))))
      (-> status
          (dissoc :valid?)))))

(defn update-status
  [data id]
  (let [status (validate-status data)]
    (if (:valid? status)
      (try
        (-> (:values status)
            (db/update-data {:id id} :statuses))
        (catch org.postgresql.util.PSQLException e
          (let [sql-state (.getSQLState e)]
            (if (= sql-state "23505")
              (-> status
                  (dissoc :valid?)
                  (assoc-in [:errors] {:name "Такой статус уже существует"}))
              (-> status
                  (dissoc :valid?)
                  (assoc-in [:errors] {:name "Какая-то ошибка базы данных"}))))))
      (-> status
          (dissoc :valid?)))))

(defn delete-status
  [id]
  (db/delete-by-key :statuses :id id))