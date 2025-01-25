(ns server.routes.users
  (:require [compojure.core :refer [GET defroutes]]

            [server.view.layout :as layout]
            [server.view.users :as view]
            [server.models.users :refer [list-users]])
  (:gen-class))

(defroutes users-routes
  (GET "/users" [] (layout/common (view/users-page (list-users)))))