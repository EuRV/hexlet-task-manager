(ns server.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.session :refer [wrap-session]]
            [compojure.core :refer [defroutes routes]]
            
            [server.middleware :refer [wrap-i18n]]
            [server.routes.home :refer [home-routes]]
            [server.routes.users :refer [users-routes]]
            [server.routes.session :refer [session-routes]]
            [server.routes.statuses :refer [statuses-routes]]
            [server.routes.tasks :refer [tasks-routes]]
            [server.routes.labels :refer [labels-routes]])
  (:gen-class))

(defroutes app-routes
  (routes home-routes
          users-routes
          session-routes
          statuses-routes
          tasks-routes
          labels-routes))

(def app
  (-> #'app-routes
      (wrap-defaults
       (-> site-defaults 
           (assoc-in [:params :keywordize] true)
           (assoc-in [:security :anti-forgery] false)
           (assoc-in [:session :flash] true)))
      (wrap-cookies)
      (wrap-session)
      (wrap-i18n)))

#_{:clj-kondo/ignore [:unused-binding]}
(defn -main [& args]
  (run-jetty #'app {:port 3000}))