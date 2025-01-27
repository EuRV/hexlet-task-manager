(ns server.models.users
  (:require [server.db :as db]
            [server.helpers :refer [formatter-users]])
  (:gen-class))

(defn list-users
  []
  (mapv formatter-users (db/get-users)))