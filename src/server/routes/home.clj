(ns server.routes.home
  (:require [compojure.core :refer [GET defroutes]]
            
            [server.view.home :as view])
  (:gen-class))

(defroutes home-routes
  (GET "/" request (view/home request)))