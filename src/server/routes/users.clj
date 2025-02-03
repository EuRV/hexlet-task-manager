(ns server.routes.users
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [ring.util.response :as resp]

   [server.models.users :refer [get-users add-user]]
   [server.view.layout :as layout]
   [server.view.users :as view])
  (:gen-class))

(defroutes users-routes
  (GET "/users" {:keys [session]} (layout/common session (view/users-page (get-users))))
  (GET "/users/new" {:keys [session]} (layout/common session (view/users-new)))
  (POST "/users" req
    (add-user (req :params))
    (resp/redirect "/")))