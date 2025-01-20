(ns server.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [compojure.core :refer [defroutes routes]]
  
            [server.routes.home :refer [home-routes]]
            [server.routes.users :refer [users-routes]])
  (:gen-class))

(defroutes app-routes
  (routes home-routes
          users-routes))

#_{:clj-kondo/ignore [:unused-binding]}
(defn -main [& args]
  (run-jetty #'app-routes {:port 3000}))