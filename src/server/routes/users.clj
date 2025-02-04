(ns server.routes.users
  (:require
   [compojure.core :refer [defroutes GET POST]]
   [ring.util.response :as resp]

   [server.models.users :refer [get-users add-user validate-user]]
   [server.view.layout :as layout]
   [server.view.users :as view])
  (:gen-class))

(defroutes users-routes
  (GET "/users" {:keys [session]} (layout/common session (view/users-page (get-users))))
  (GET "/users/new" {:keys [session]} (layout/common session (view/users-new {:errors {} :values {}})))
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
         (view/users-new vval))))))