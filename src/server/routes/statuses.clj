(ns server.routes.statuses
  (:require
   [compojure.core :refer [defroutes GET POST PATCH DELETE]]
   [ring.util.response :as resp]

   [server.models.statuses :as models]
   [server.view.statuses :as view]
   [server.helpers :refer [to-number clean-data]])
  (:gen-class))

(defn statuses-handler
  [request]
  (if (seq (:session request))
    (try
      (view/statuses-page request (models/get-statuses))
      (catch Exception e
        (-> request
         (assoc :flash {:type "danger" :message (ex-message e)})
         (view/statuses-page))))
    (resp/redirect "/")))

(defn statuses-new-handler
  ([request] (statuses-new-handler request {:errors {} :values {}}))
  ([request data]
   (if (seq (:session request))
     (view/statuses-new request data)
     (resp/redirect "/"))))

(defn statuses-edit-handler
  [request]
  (let [status-id (-> request :params :id to-number)]
    (view/statuses-edit request {:errors {} :values (models/get-status status-id)})))

(defn statuses-create-handler
  [request]
  (let [data (-> request :params models/create-statuses)]
    (if (:errors data)
      (-> request
          (assoc :flash {:type "danger" :message "Не удалось создать статус"})
          (view/statuses-new data))
      (-> (resp/redirect "/statuses")
          (assoc :flash {:type "info" :message "Статус успешно создан"})))))

(defn statuses-update-handler
  [request]
  (let [status-id (-> request
                      :params
                      :id
                      to-number)
        data (-> request
                 :params
                 (clean-data #{:name})
                 (models/update-status status-id))]
    (if (:errors data)
      (-> request
          (assoc :flash {:type "danger" :message "Не удалось изменить статус"})
          (view/statuses-edit data))
      (-> (resp/redirect "/statuses")
          (assoc :flash {:type "info" :message "Статус успешно изменён"})))))

(defn statuses-delete-handler
  [request]
  (let [status-id (-> request :params :id to-number)]
    (try
      (models/delete-status status-id)
      (-> (resp/redirect "/statuses")
          (assoc :flash {:type "info" :message "Статус успешно удалён"}))
      (catch Exception _
        (-> (resp/redirect "/statuses")
            (assoc :flash {:type "danger" :message "Не удалось удалить статус"}))))))

(defroutes statuses-routes
  (GET "/statuses" request (statuses-handler request))
  (GET "/statuses/new" request (statuses-new-handler request))
  (GET "/statuses/:id/edit" request (statuses-edit-handler request))
  (POST "/statuses" request (statuses-create-handler request))
  (PATCH "/statuses/:id" request (statuses-update-handler request))
  (DELETE "/statuses/:id" request (statuses-delete-handler request)))