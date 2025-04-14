(ns server.models.users
  (:require
   [buddy.hashers :as hashers]
   [clojure.spec.alpha :as s]
   [server.db.sql.queries :as db]
   [server.helpers :refer [validate-data clean-data]])
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

(def validate-user
  (validate-data :users/person spec-errors))

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
  [data]
  (let [user (-> data
                 (clean-data #{:first-name :last-name :email :password-digest})
                 validate-user)]
    (if (:valid? user)
      (try
        (-> (:values user)
            (update :password-digest #(hashers/encrypt % {:algorithm :bcrypt}))
            (db/insert-data :users))
        (catch org.postgresql.util.PSQLException e
          (let [sql-state (.getSQLState e)]
            (if (= sql-state "23505")
              (-> user
                  (dissoc :valid?)
                  (assoc-in [:errors] {:email "Пользователь с таким email уже существует"}))
              (-> user
                  (dissoc :valid?)
                  (assoc-in [:errors] {:name "Какая-то ошибка базы данных"}))))))
      (-> user
          (dissoc :valid?)))))

(defn update-user
  [data id]
  (let [user (-> data
                 (clean-data #{:first-name :last-name :email :password-digest})
                 validate-user)]
     (if (:valid? user)
       (try
         (-> (:values user)
             (update :password-digest #(hashers/encrypt % {:algorithm :bcrypt}))
             (db/update-data {:id id} :users))
         (catch org.postgresql.util.PSQLException e
           (let [sql-state (.getSQLState e)]
             (if (= sql-state "23505")
               (-> user
                   (dissoc :valid?)
                   (assoc-in [:errors] {:email "Пользователь с таким email уже существует"}))
               (-> user
                   (dissoc :valid?)
                   (assoc-in [:errors] {:name "Какая-то ошибка базы данных"}))))))
       (-> user
           (dissoc :valid?)))))

(defn delete-user
  [id]
  (db/delete-by-key :users :id id))