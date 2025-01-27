(ns server.models.users
  (:require [clojure.spec.alpha :as s]

            [server.db :as db]
            [server.helpers :refer [formatter-users]])
  (:gen-class))

(s/def :user/first-name
  (s/and string? #(>= (count %) 1)))

(s/def :user/last-name
  (s/and string? #(>= (count %) 1)))

(s/def :user/email
  (s/and  string? (partial re-matches #"(.+?)@(.+?)\.(.+?)")))

(s/def :user/password-digest
  (s/and string? #(>= (count %) 3)))

(s/def :user/person
  (s/keys :req [:user/first-name
                :user/last-name
                :user/email
                :user/password-digest]))

(defn list-users
  []
  (mapv formatter-users (db/get-users)))