(ns server.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [compojure.core :refer [defroutes routes]]
            
            [server.routes.home :refer [home-routes]]
            [server.routes.users :refer [users-routes]]
            [server.routes.session :refer [session-routes]])
  (:gen-class))

(defroutes app-routes
  (routes home-routes
          users-routes
          session-routes))

(def app
  (-> #'app-routes
      wrap-keyword-params
      wrap-params))

#_{:clj-kondo/ignore [:unused-binding]}
(defn -main [& args]
  (run-jetty #'app {:port 3000}))