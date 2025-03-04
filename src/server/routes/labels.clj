(ns server.routes.labels
  (:require
   [compojure.core :refer [defroutes GET]]
   [ring.util.response :as resp]
  
   [server.models.labels :as models]
   [server.view.labels :as view]
   [server.helpers :refer [to-number clean-data]])
  (:gen-class))

(defn labels-handler
  [request]
  (if (seq (:session request))
    (let [content (models/get-labels)]
      (if (seq (:error content))
        (->
         (assoc request :flash {:type "danger" :message (-> content :error :message)})
         (view/labels-page (-> content :error :value)))
        (view/labels-page request content)))
    (resp/redirect "/")))

(defroutes labels-routes
  (GET "/labels" request (labels-handler request)))