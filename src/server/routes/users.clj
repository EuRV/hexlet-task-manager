(ns server.routes.users
  (:require
   [compojure.core :refer [defroutes GET POST PATCH DELETE]]
   [ring.util.response :as resp]

   [server.models.users :as models]
   [server.view.users :as view]
   [server.helpers :refer [to-number clean-data]])
  (:gen-class))

(defn users-handler
  [request]
  (try
    (view/users-page request (models/get-users))
    (catch Exception e
      (-> (assoc request :flash {:type "danger" :message (ex-message e)})
          (view/users-page)))))

(defn user-new-handler
  ([request] (user-new-handler request {:errors {} :values {}}))
  ([request data]
   (view/users-new request data)))

(defn user-create-handler
  [request]
  (let [data (-> request :params models/create-user)]
    (if (:errors data)
      (view/users-new request data)
      (-> (resp/redirect "/")
          (assoc :flash {:type "info" :message "Пользователь успешно зарегистрирован"})))))

(defn user-edit-handler
  [request]
  (let [user-id (-> request :params :id to-number)
        session-user-id (-> request :session :user-id to-number)]
    (if (not= user-id session-user-id)
      (-> (resp/redirect "/users")
          (assoc :flash {:type "danger" :message "Вы не можете редактировать или удалять другого пользователя"}))
      (view/users-edit request {:errors {} :values (models/get-user user-id {:columns [:id :first-name :last-name :email]})}))))

(defn user-update-handler
  [request]
  (let [user-id (-> request
                    :params
                    :id
                    to-number)
        data (-> request
                 :params
                 (clean-data #{:first-name :last-name :email :password-digest})
                 (models/update-user user-id))]
    (if (:errors data)
      (view/users-edit request data)
      (-> (resp/redirect "/users")
          (assoc :flash {:type "info" :message "Пользователь успешно изменён"})))))

(defn user-delete-handler
  [request]
  (let [user-id (-> request :params :id to-number)
        session-user-id (-> request :session :user-id to-number)]
    (if (not= user-id session-user-id)
      (-> (resp/redirect "/users")
          (assoc :flash {:type "danger" :message "Вы не можете редактировать или удалять другого пользователя"}))
      (do
        (models/delete-user user-id)
        (let [session (:session request)
              updated-session (dissoc session :user-id :email)]
          (-> (resp/redirect "/users")
              (assoc :session updated-session)))))))

(defroutes public-users-routes
  (GET "/users" request (users-handler request))
  (GET "/users/new" request (user-new-handler request))
  (POST "/users" request (user-create-handler request)))

(defroutes protected-users-routes
  (GET "/users/:id/edit" request (user-edit-handler request))
  (PATCH "/users/:id" request (user-update-handler request))
  (DELETE "/users/:id" request (user-delete-handler request)))