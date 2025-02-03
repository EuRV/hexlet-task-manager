(ns server.routes.session
  (:require
   [compojure.core :refer [defroutes GET POST DELETE]]
   [ring.util.response :as resp]

   [server.view.layout :as layout]
   [server.view.session :as view]
   [server.db :as db])
  (:gen-class))

(defn authenticate [email password]
  (let [[user] (db/query-by-key
              :users
              {:email email :password-digest password}
              {:columns [:id :email :password-digest]})]
    (when (and user (= password (:users/password-digest user)))
      user)))

(defn login-handler [request]
  (let [email (request :email)
        password (request :password-digest)
        user (authenticate email password)]
    (if user
      (-> (resp/redirect "/")
          (assoc :session {:id (:users/id user)
                           :email (:users/email user)}))
      (layout/common (view/login {:error {:email email :message "Неправильный емейл или пароль"}})))))

(defroutes session-routes
  (GET "/session/new" [] (layout/common (view/login {})))
  (POST "/session" request
    (login-handler (request :params)))
  (DELETE "/session" [] ()))