(ns server.routes.statuses
  (:require
   [compojure.core :refer [defroutes GET]]
   
   [server.models.statuses :as models]
   [server.view.layout :as layout]
   [server.view.statuses :as view])
  (:gen-class))

(defroutes statuses-routes
  (GET "/statuses" request (layout/common request :statuses (view/statuses-page (models/get-statuses)))))