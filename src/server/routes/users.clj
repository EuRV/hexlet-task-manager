(ns server.routes.users
  (:require
   [compojure.core :refer [defroutes GET POST PATCH DELETE]]
   [ring.util.response :as resp]

   [server.models.users :refer [get-users get-user add-user validate-user delete-user]]
   [server.view.layout :as layout]
   [server.view.users :as view]
   [server.helpers :refer [to-number]])
  (:gen-class))

(defn users-handler
  [request]
  (let [users (get-users)
        session (:session request)]
    (layout/common
     session
     (view/users-page users))))

(defn users-new-handler
  [request]
  (let [session (:session request)]
    (layout/common
     session
     (view/users-new {:errors {} :values {}}))))

(defn users-create-handler
  [request]
  (let [data (-> request :params validate-user)
        session (:session request)]
    (if (:valid? data)
      (try
        (add-user (:values data))
        (resp/redirect "/")
        (catch Exception _
          (layout/common
           session
           (view/users-new (assoc-in data [:errors :email] "Такой email уже существует")))))
      (layout/common
       session
       (view/users-new data)))))

(defn users-edit-handler
  [request]
  (let [user-id (-> request :params :id to-number)
        session-user-id (-> request :session :user-id to-number)
        session (:session request)]
    (cond
      (nil? session-user-id) (resp/redirect "/")
      (not= user-id session-user-id) (resp/redirect "/users")
      :else (layout/common
             session
             (view/users-edit {:errors {} :values (get-user user-id)})))))

(defn users-update-handler
  [request]
  (-> request :params println))

(defn users-delete-handler
  [request]
  (let [user-id (-> request :params :id to-number)
        session-user-id (-> request :session :user-id to-number)]
    (cond
      (nil? session-user-id) (resp/redirect "/")
      (not= user-id session-user-id) (resp/redirect "/users")
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