(ns server.routes.tasks
  (:require
   [compojure.core :refer [defroutes GET POST PATCH DELETE]]
   [ring.util.response :as resp]
  
   [server.models.tasks :as models]
   [server.models.statuses :refer [get-statuses]]
   [server.models.users :refer [get-users]]
   [server.view.tasks :as view]
   [server.helpers :as h])
  (:gen-class))

(defn tasks-handler
  [{:keys [session params] :as request}]
  (if (seq session)
    (let [{:keys [status executor labels is-creator-user]} params
          params-query (->>
                        [(when status [:status-id (h/to-number status)])
                         (when executor [:executor-id (h/to-number executor)])
                         (when labels [:labels-id (h/to-number labels)])
                         (when is-creator-user [:creator-id (-> request :session :user-id)])]
                        (remove #(nil? (second %)))
                        (into {}))
          content (models/get-tasks params-query)]
      (if (seq (:error content))
        (->
         (assoc request :flash {:type "danger" :message (-> content :error :message)})
         (view/tasks-page (-> content :error :value) (get-statuses) (get-users) []))
        (view/tasks-page request content (get-statuses) (get-users) [])))
    (resp/redirect "/")))

(defn task-new-handler
  ([request] (task-new-handler request {:errors {} :values {}}))
  ([request data]
   (view/task-new request data (get-statuses) (get-users) [])))

(defn task-view-handler
  [request]
  (view/task-page request (models/get-task (-> request :params :id h/to-number))))

(defn task-create-handler
  [{:keys [params session] :as request}]
  (let [task (->
              params
              (assoc :creator-id (:user-id session))
              h/clean-task-data
              models/validate-task)]
    (if (:valid? task)
      (try
        (models/create-task (:values task))
        (->
         (resp/redirect "/tasks")
         (assoc :flash {:type "info" :message "Задача успешно создана"}))
        (catch Exception _
          (->
           request
           (assoc :flash {:type "danger" :message "Ошибка базы данных"})
           (view/task-new task (get-statuses) (get-users) []))))
      (do
        (println task)
        (->
         request
         (assoc :flash {:type "danger" :message "Не удалось создать задачу"})
         (view/task-new task (get-statuses) (get-users) []))))))

(defroutes tasks-routes
  (GET "/tasks" request (tasks-handler request))
  (GET "/tasks/new" request (task-new-handler request))
  (GET "/tasks/:id" request (task-view-handler request))
  (POST "/tasks" request (task-create-handler request)))