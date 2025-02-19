(ns server.routes.tasks
  (:require
   [compojure.core :refer [defroutes GET POST PATCH DELETE]]
   [ring.util.response :as resp]
  
   [server.models.tasks :as models]
   [server.view.tasks :as view])
  (:gen-class))

(defn tasks-handler
  [request]
  (if (seq (:session request))
    (let [content (models/get-tasks)]
      (if (seq (:error content))
        (->
         (assoc request :flash {:type "danger" :message (-> content :error :message)})
         (view/tasks-page (-> content :error :value)))
        (view/tasks-page request content)))
    (resp/redirect "/")))

(defroutes tasks-routes
  (GET "/tasks" request (tasks-handler request)))