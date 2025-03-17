(ns server.models.users
  (:require [clojure.spec.alpha :as s]

            [server.db.sql.queries :as db])
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
  (db/query-database ["SELECT
                         id,
                         CONCAT(first_name, ' ', last_name) AS fname,
                         email,
                         TO_CHAR(created_at, 'FMMM/FMDD/YYYY, HH12:MI:SS AM') AS created_at
                       FROM users
                       ORDER BY id ASC"]))

(defn get-user
  ([id] (get-user id {:columns [:id :first-name :last-name :email :password-digest]}))
  ([id columns] (db/query-by-id :users id columns)))

(defn get-user-by-email
  [email]
  (db/query-by-key
   :users
   {:email email}
   {:columns [:id :email :password-digest]}))

(defn create-user
  [user]
  (db/insert-data :users user))

(defn update-user
  [values id]
  (db/update-data :users values {:id id}))

(defn delete-user
  [id]
  (db/delete-by-key :users :id id))