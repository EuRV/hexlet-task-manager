(ns server.routes.users
  (:require [compojure.core :refer [GET POST defroutes]]

            [server.view.layout :as layout]
            [server.view.users :as view]
            [server.models.users :refer [list-users]])
  (:gen-class))

(defroutes users-routes
  (GET "/users" [] (layout/common (view/users-page (list-users))))
  (GET "/users/new" [] (layout/common (view/users-new)))
  (POST "/users" req (println req)))