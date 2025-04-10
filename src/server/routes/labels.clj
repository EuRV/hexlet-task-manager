(ns server.routes.labels
  (:require
   [compojure.core :refer [defroutes GET POST PATCH DELETE]]
   [ring.util.response :as resp]
  
   [server.models.labels :as models]
   [server.view.labels :as view]
   [server.helpers :refer [to-number clean-data]])
  (:gen-class))

(defn labels-handler
  [request]
  (try
    (view/labels-page request (models/get-labels))
    (catch Exception e
      (->
       (assoc request :flash {:type "danger" :message (ex-message e)})
       (view/labels-page)))))

(defn label-new-handler
  ([request] (label-new-handler request {:errors {} :values {}}))
  ([request data] (view/labels-new-page request data)))

(defn label-edit-handler
  [request]
  (let [label-id (-> request :params :id to-number)]
    (view/labels-edit-page request {:errors {} :values (models/get-label label-id)})))

(defn label-create-handler
  [request]
  (let [data (-> request :params models/create-label)]
    (if (:errors data)
      (-> request
          (assoc :flash {:type "danger" :message "Не удалось создать метку"})
          (view/labels-new-page data))
      (-> (resp/redirect "/labels")
          (assoc :flash {:type "info" :message "Метка успешно создана"})))))

(defn label-update-handler
  [request]
  (let [label-id (-> request :params :id to-number)
        data (-> request :params (clean-data #{:name}) (models/update-label label-id))]
    (if (:errors data)
      (-> request
          (assoc :flash {:type "danger" :message "Не удалось изменить метку"})
          (view/labels-edit-page data))
      (-> (resp/redirect "/labels")
          (assoc :flash {:type "info" :message "Метка успешно изменена"})))))

(defn label-delete-handler
  [request]
  (let [label-id (-> request :params :id to-number)]
    (try (models/delete-label label-id)
         (->
          (resp/redirect "/labels")
          (assoc :flash {:type "info" :message "Метка успешно удалена"}))
         (catch Exception _
           (->
            (resp/redirect "/labels")
            (assoc :flash {:type "danger" :message "Не удалось удалить метку"}))))))

(defroutes labels-routes
  (GET "/labels" request (labels-handler request))
  (GET "/labels/new" request (label-new-handler request))
  (GET "/labels/:id/edit" request (label-edit-handler request))
  (POST "/labels" request (label-create-handler request))
  (PATCH "/labels/:id" request (label-update-handler request))
  (DELETE "/labels/:id" request (label-delete-handler request)))