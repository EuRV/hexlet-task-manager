(ns server.models.users
  (:require [server.db :as db])
  (:gen-class))

(defn formatter-date [date]
  (.format (java.text.SimpleDateFormat. "MM/dd/yyyy HH:mm:ss") date))

(defn formatter-fname [fname lname]
  (format "%s %s" fname lname))

(defn formatter-users [user]
  (let [{:keys [users/id users/first-name users/last-name users/email users/created-at]} user]
    {:id id
     :fname (formatter-fname first-name last-name)
     :email email
     :date (formatter-date created-at)}))

(defn list-users
  []
  (mapv formatter-users (db/get-users)))