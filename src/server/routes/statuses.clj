(ns server.routes.statuses
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [ring.util.response :as resp]

   [server.models.statuses :as models]
   [server.view.statuses :as view])
  (:gen-class))

(defn statuses-handler
  [request]
  (if (seq (:session request))
    (let [content (models/get-statuses)]
     (if (seq (:error content))
       (->
        (assoc request :flash {:type "danger" :message (-> content :error :message)})
        (view/statuses-page (-> content :error :value)))
       (view/statuses-page request content)))
    (resp/redirect "/")))

(defn statuses-new-handler
  ([request] (statuses-new-handler request {:errors {} :values {}}))
  ([request data]
   (if (seq (:session request))
     (view/statuses-new request data)
     (resp/redirect "/"))))

(defroutes statuses-routes
  (GET "/statuses" request (statuses-handler request))
  (GET "/statuses/new" request (statuses-new-handler request)))