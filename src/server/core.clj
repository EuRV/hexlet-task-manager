(ns server.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes routes]]
            
            [server.routes.home :refer [home-routes]]
            [server.routes.users :refer [users-routes]])
  (:gen-class))

(defroutes app-routes
  (routes home-routes
          users-routes)) 

(def app (wrap-params #'app-routes))

#_{:clj-kondo/ignore [:unused-binding]}
(defn -main [& args]
  (run-jetty #'app {:port 3000}))