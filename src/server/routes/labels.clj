(ns server.routes.labels
  (:require
   [compojure.core :refer [defroutes GET POST PATCH]]
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

(defroutes labels-routes
  (GET "/labels" request (labels-handler request))
  (GET "/labels/new" request (label-new-handler request))
  (GET "/labels/:id/edit" request (label-edit-handler request))
  (POST "/labels" request (label-create-handler request))
  (PATCH "/labels/:id" request (label-update-handler request)))