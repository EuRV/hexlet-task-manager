(ns server.routes.tasks
  (:require
   [compojure.core :refer [defroutes GET POST PATCH DELETE]]
   [ring.util.response :as resp]
  
   [server.models.tasks :as models]
   [server.models.statuses :refer [get-statuses]]
   [server.models.users :refer [get-users]]
   [server.models.labels :refer [get-labels]]
   [server.view.tasks :as view]
   [server.helpers :as h]
   [server.db.sql.queries :as db])
  (:gen-class))

(defn tasks-handler
  [{:keys [session params] :as request}]
  (if (seq session)
    (let [{:keys [status executor label is-creator-user]} params
          params-query (->>
                        [(when status [:status-id (h/to-number status)])
                         (when executor [:executor-id (h/to-number executor)])
                         (when label [:label-id (h/to-number label)])
                         (when is-creator-user [:creator-id (-> request :session :user-id)])]
                        (remove #(nil? (second %)))
                        (into {}))
          content (models/get-tasks params-query)]
      (if (seq (:error content))
        (->
         (assoc request :flash {:type "danger" :message (-> content :error :message)})
         (view/tasks-page (-> content :error :value) (get-statuses) (get-users) (get-labels)))
        (view/tasks-page request content (get-statuses) (get-users) (get-labels))))
    (resp/redirect "/")))

(defn task-new-handler
  ([request] (task-new-handler request {:errors {} :values {}}))
  ([{:keys [session] :as request} data]
   (if (seq session)
     (view/task-new request data (get-statuses) (get-users) (get-labels))
     (resp/redirect "/"))))

(defn task-view-handler
  [request]
  (view/task-page request (models/get-task-relation (-> request :params :id h/to-number))))

(defn task-edit-handler
  [request]
  (let [task-id (-> request :params :id h/to-number)]
   (view/task-edit request {:errors {} :values (models/get-task task-id)} (get-statuses) (get-users) (get-labels))))

(defn task-create-handler
  [{:keys [params session] :as request}]
  (let [task (->
              params
              (assoc :creator-id (:user-id session))
              h/clean-task-data
              models/validate-task)]
    (if (:valid? task)
      (try
        (db/create-task-with-labels (dissoc (:values task) :labels) (:labels (:values task)))
        (->
         (resp/redirect "/tasks")
         (assoc :flash {:type "info" :message "Задача успешно создана"}))
        (catch Exception _
          (->
           request
           (assoc :flash {:type "danger" :message "Ошибка базы данных"})
           (view/task-new task (get-statuses) (get-users) (get-labels)))))
      (->
       request
       (assoc :flash {:type "danger" :message "Не удалось создать задачу"})
       (view/task-new task (get-statuses) (get-users) (get-labels))))
    ))

(defn task-update-handler
  [{:keys [params session] :as request}]
  (let [task (->
              params
              (assoc :creator-id (:user-id session))
              h/clean-task-data
              models/validate-task)]
    (if (:valid? task)
      (try
        (db/update-task-with-labels (-> params :id h/to-number) (dissoc (:values task) :labels) (:labels (:values task)))
        (->
         (resp/redirect "/tasks")
         (assoc :flash {:type "info" :message "Задача успешно изменена"}))
        (catch Exception _
          (->
           request
           (assoc :flash {:type "danger" :message "Ошибка базы данных"})
           (view/task-edit task (get-statuses) (get-users) (get-labels)))))
      (->
       request
       (assoc :flash {:type "danger" :message "Не удалось изменить задачу"})
       (view/task-edit task (get-statuses) (get-users) (get-labels))))))

(defn task-delete-handler
  [request]
  (let [task-id (-> request :params :id h/to-number)
        session-user-id (-> request :session :user-id h/to-number)
        creator-id (-> task-id models/get-task :creator-id)]
    (if (= session-user-id creator-id)
      (try
        (db/delete-task-with-labels task-id)
        (->
         (resp/redirect "/tasks")
         (assoc :flash {:type "info" :message "Задача успешно удалена"}))
        (catch Exception _
         (->
          (resp/redirect "/tasks")
          (assoc :flash {:type "danger" :message "Ошибка базы данных"}))))
      (->
       (resp/redirect "/tasks")
       (assoc :flash {:type "danger" :message "Задачу может удалить только её автор"})))))

(defroutes tasks-routes
  (GET "/tasks" request (tasks-handler request))
  (GET "/tasks/new" request (task-new-handler request))
  (GET "/tasks/:id" request (task-view-handler request))
  (GET "/tasks/:id/edit" request (task-edit-handler request))
  (POST "/tasks" request (task-create-handler request))
  (PATCH "/tasks/:id" request (task-update-handler request))
  (DELETE "/tasks/:id" request (task-delete-handler request)))