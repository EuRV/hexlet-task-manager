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
  (if (seq (:session request))
    (try
      (view/labels-page request (models/get-labels))
      (catch Exception e
        (->
         (assoc request :flash {:type "danger" :message (ex-message e)})
         (view/labels-page))))
    (resp/redirect "/")))

(defn label-new-handler
  ([request] (label-new-handler request {:errors {} :values {}}))
  ([request data]
   (if (seq (:session request))
     (view/labels-new-page request data)
     (resp/redirect "/"))))

(defn label-edit-handler
  [request]
  (let [label-id (-> request :params :id to-number)]
    (view/labels-edit-page request {:errors {} :values (models/get-label label-id)})))

(defn label-create-handler
  [request]
  (let [data (-> request :params models/validate-label)]
    (if (:valid? data)
      (try
        (models/create-label (:values data))
        (->
         (resp/redirect "/labels")
         (assoc :flash {:type "info" :message "Метка успешно создана"}))
        (catch Exception _
          (->
           request
           (assoc :flash {:type "danger" :message "Не удалось создать метку"})
           (view/labels-new-page (assoc-in data [:errors :name] "Такая метка уже существует")))))
      (->
       request
       (assoc :flash {:type "danger" :message "Не удалось создать метку"})
       (view/labels-new-page data)))))

(defn label-update-handler
  [request]
  (let [label-id (-> request :params :id to-number)
        data (-> request :params (clean-data #{:name}) models/validate-label)]
    (if (:valid? data)
      (try
        (models/update-label label-id (:values data))
        (->
         (resp/redirect "/labels")
         (assoc :flash {:type "info" :message "Метка успешно изменена"}))
        (catch Exception e
          (println (ex-message e))))
      (->
       request
       (assoc :flash {:type "danger" :message "Не удалось изменить метку"})
       (view/labels-edit-page data)))))

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