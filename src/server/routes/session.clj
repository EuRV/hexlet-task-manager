(ns server.routes.session
  (:require
   [compojure.core :refer [defroutes GET POST DELETE]]
   [ring.util.response :as resp]

   [server.view.session :as view]
   [server.models.users :refer [get-user-by-email-password]])
  (:gen-class))

(defn session-new-handler
  [request]
  (view/login request {}))

(defn authenticate [email password]
  (let [[user] (get-user-by-email-password email password)]
    (when user user)))

(defn login-handler [request]
  (let [email (-> request :params :email)
        password (-> request :params :password-digest)
        user (authenticate email password)]
    (if user
      (-> (resp/redirect "/")
          (assoc :flash {:type "success" :message "Вы залогинены"})
          (assoc :session {:user-id (:id user)
                           :email (:email user)}))
      (view/login request {:error {:email email :message "Неправильный емейл или пароль"}}))))

(defn clear-session [request]
  (let [session (:session request)
        updated-session (dissoc session :user-id :email)]
    (-> (resp/redirect "/")
        (assoc :flash {:type "info" :message "Вы разлогинены"})
        (assoc :session updated-session))))

(defroutes session-routes
  (GET "/session/new"
    request
    (session-new-handler request))
  (POST "/session"
    request
    (login-handler request))
  (DELETE "/session"
    request
    (clear-session request)))