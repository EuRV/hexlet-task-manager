(ns server.routes.users
  (:require
   [compojure.core :refer [defroutes GET POST PATCH DELETE]]
   [ring.util.response :as resp]
   [buddy.hashers :as hashers]

   [server.models.users :refer [get-users get-user add-user validate-user delete-user update-user]]
   [server.view.users :as view]
   [server.helpers :refer [to-number clean-data]])
  (:gen-class))

(defn users-handler
  [request]
  (let [users (get-users)]
    (if (seq (:error users))
      (->
       (assoc request :flash {:type "danger" :message (-> users :error :message)})
       (view/users-page (-> users :error :value)))
      (view/users-page request users))))

(defn users-new-handler
  ([request] (users-new-handler request {:errors {} :values {}}))
  ([request data]
   (view/users-new request data)))

(defn users-create-handler
  [request]
  (let [data (-> request :params validate-user)]
    (if (:valid? data)
      (try
        (-> (:values data)
            (update :password-digest #(hashers/encrypt % {:algorithm :bcrypt}))
            add-user)
        (->
         (resp/redirect "/")
         (assoc :flash {:type "info" :message "Пользователь успешно зарегистрирован"}))
        (catch Exception _
          (view/users-new request (assoc-in data [:errors :email] "Такой email уже существует"))))
      (view/users-new request data))))

(defn users-edit-handler
  [request]
  (let [user-id (-> request :params :id to-number)
        session-user-id (-> request :session :user-id to-number)]
    (cond
      (nil? session-user-id)
      (->
       (resp/redirect "/")
       (assoc :flash {:type "danger" :message "Доступ запрещён! Пожалуйста, авторизируйтесь."}))
      (not= user-id session-user-id)
      (->
       (resp/redirect "/users")
       (assoc :flash {:type "danger" :message "Вы не можете редактировать или удалять другого пользователя"}))
      :else (view/users-edit request {:errors {} :values (get-user user-id)}))))

(defn users-update-handler
  [request]
  (let [user-id (-> request :params :id to-number)
        data (-> request :params (clean-data #{:first-name :last-name :email :password-digest}) validate-user)]
    (if (:valid? data)
      (try
        (-> (:values data)
            (update :password-digest #(hashers/encrypt % {:algorithm :bcrypt}))
            (update-user user-id))
        (->
         (resp/redirect "/users")
         (assoc :flash {:type "info" :message "Пользователь успешно изменён"}))
        (catch Exception e
          (println (ex-message e))))
      (view/users-edit request data))))

(defn users-delete-handler
  [request]
  (let [user-id (-> request :params :id to-number)
        session-user-id (-> request :session :user-id to-number)]
    (cond
      (nil? session-user-id)
      (->
       (resp/redirect "/")
       (assoc :flash {:type "danger" :message "Доступ запрещён! Пожалуйста, авторизируйтесь."}))
      (not= user-id session-user-id)
      (->
       (resp/redirect "/users")
       (assoc :flash {:type "danger" :message "Вы не можете редактировать или удалять другого пользователя"}))
      :else (do
              (delete-user user-id)
              (let [session (:session request)
                    updated-session (dissoc session :user-id :email)]
                (-> (resp/redirect "/users")
                    (assoc :session updated-session)))))))

(defroutes users-routes
  (GET "/users" request (users-handler request))
  (GET "/users/new" request (users-new-handler request))
  (GET "/users/:id/edit" request (users-edit-handler request))
  (POST "/users" request (users-create-handler request))
  (PATCH "/users/:id" request (users-update-handler request))
  (DELETE "/users/:id" request (users-delete-handler request)))