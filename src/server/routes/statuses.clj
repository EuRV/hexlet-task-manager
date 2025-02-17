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

(defn statuses-create-handler
  [request]
  (let [data (-> request :params models/validate-statuses)]
    (if (:valid? data)
      (try
        (models/create-statuses (:values data))
        (->
         (resp/redirect "/statuses")
         (assoc :flash {:type "info" :message "Статус успешно создан"}))
        (catch Exception _
          (->
           request
           (assoc :flash {:type "danger" :message "Не удалось создать статус"})
           (view/statuses-new (assoc-in data [:errors :name] "Такой статус уже существует")))))
      (view/statuses-new request data))))

(defroutes statuses-routes
  (GET "/statuses" request (statuses-handler request))
  (GET "/statuses/new" request (statuses-new-handler request))
  (POST "/statuses" request (statuses-create-handler request)))