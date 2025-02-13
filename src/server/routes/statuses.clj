(ns server.routes.statuses
  (:require
   [compojure.core :refer [defroutes GET]]
   
   [server.models.statuses :as models]
   [server.view.statuses :as view])
  (:gen-class))

(defroutes statuses-routes
  (GET "/statuses" request (view/statuses-page request (models/get-statuses))))