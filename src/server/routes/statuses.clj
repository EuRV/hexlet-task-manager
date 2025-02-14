(ns server.routes.statuses
  (:require
   [compojure.core :refer [defroutes GET]]

   [server.models.statuses :as models]
   [server.view.statuses :as view])
  (:gen-class))

(defn statuses-handler
  [request content]
  (view/statuses-page request content))

(defn statuses-new-handler
  [request]
  (view/statuses-new request))

(defroutes statuses-routes
  (GET "/statuses" request (statuses-handler request (models/get-statuses)))
  (GET "/statuses/new" request (statuses-new-handler request)))