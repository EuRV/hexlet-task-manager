(ns server.models.users
  (:require [clojure.spec.alpha :as s]

            [server.db :as db]
            [server.db.sql.queries :refer [queries]]
            [server.helpers :refer [formatter-users]])
  (:gen-class))

(defn at-least [n]
  (fn [data]
    (>= (count data) n)))

(s/def ::string string?)

(s/def ::at-least-one
  (at-least 1))

(s/def ::at-least-three
  (at-least 3))

(s/def ::email
  (partial re-matches #"(.+?)@(.+?)\.(.+?)"))

(s/def :users/first-name
  (s/and ::string ::at-least-one))

(s/def :users/last-name
  (s/and ::string ::at-least-one))

(s/def :users/email
  (s/and ::string ::email))

(s/def :users/password-digest
  (s/and ::string ::at-least-three))

(s/def :users/person
  (s/keys :req-un [:users/first-name
                :users/last-name
                :users/email
                :users/password-digest]))

(def spec-errors
  {::at-least-one "Должно быть не менее одного символа"
   ::string "Должна быть строкой"
   ::email "Не соответствует формату email"
   ::at-least-three "Должно быть не менее трех символов"})

(defn validate-user
  "Validates a user map and returns a map with :valid? and :errors keys."
  [user]
  (if (s/valid? :users/person user)
    {:valid? true :errors {} :values user}
    {:valid? false
     :errors (->> (s/explain-data :users/person user)
                  :clojure.spec.alpha/problems
                  (reduce (fn
                            [init problem]
                            (assoc init (-> problem :in last) (get spec-errors (-> problem :via peek))))
                          {}))
     :values (->> (s/explain-data :users/person user)
                  :clojure.spec.alpha/value)}))

(defn get-users
  []
  (mapv formatter-users (db/query-database (queries :list-users))))

(defn add-user
  [user]
  (db/insert-data :users user))

(defn delete-user
  [id]
  (db/delete-by-key :users :id id))