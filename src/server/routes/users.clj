(ns server.routes.users
  (:require [compojure.core :refer [GET defroutes]]

            [server.view.layout :as layout]
            [server.view.users :as view])
  (:gen-class))

(def content {:page-name "Пользователи"
              :page (view/users)})

(defn users
  []
  (layout/common content))

(defroutes users-routes
  (GET "/users" [] (users)))