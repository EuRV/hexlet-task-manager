(ns server.routes.labels
  (:require
   [compojure.core :refer [defroutes GET POST]]
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

(defn label-create-handler
  [request]
  (let [data (-> request :params models/validate-labels)]
    (if (:valid? data)
      (try
        (models/create-labels (:values data))
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

(defroutes labels-routes
  (GET "/labels" request (labels-handler request))
  (GET "/labels/new" request (label-new-handler request))
  (POST "/labels" request (label-create-handler request)))