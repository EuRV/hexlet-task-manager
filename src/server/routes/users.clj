(ns server.routes.users
  (:require
   [compojure.core :refer [defroutes GET POST PATCH DELETE]]
   [ring.util.response :as resp]

   [server.models.users :refer [get-users add-user validate-user delete-user]]
   [server.view.layout :as layout]
   [server.view.users :as view]
   [server.helpers :refer [to-number]])
  (:gen-class))

(defn delete-user-handler
  [request]
  (let [user-id (-> request :params :id to-number)
        session-user-id (-> request :session :user-id to-number)]
    (cond
      (nil? session-user-id) (println session-user-id)
      (not= user-id session-user-id) (println (type user-id) (type session-user-id))
      :else (do
              (delete-user user-id)
              (let [session (:session request)
                    updated-session (dissoc session :user-id :email)]
                (-> (resp/redirect "/users")
                    (assoc :session updated-session)))))))

(defroutes users-routes
  (GET "/users" {:keys [session]} (layout/common session (view/users-page (get-users))))
  (GET "/users/new" {:keys [session]} (layout/common session (view/users-new {:errors {} :values {}})))
  (GET "/users/:id/edit" request ())
  (POST "/users" request
    (let [data (:params request)
          vval (validate-user data)]
      (if (:valid? vval)
        (try
          (add-user (:values vval))
          (resp/redirect "/")
          (catch Exception e
            (layout/common
             (:session request)
             (view/users-new (assoc-in vval [:errors :email] "Такой email уже существует")))))
        (layout/common
         (:session request)
         (view/users-new vval)))))
  (PATCH "/users/:id" request ())
  (DELETE "/users/:id" request (delete-user-handler request)))