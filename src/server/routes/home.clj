(ns server.routes.home
  (:require [compojure.core :refer [GET defroutes]]

            [server.view.layout :as layout]
            [server.view.home :as view])
  (:gen-class))

(defn home
  []
  (layout/common (view/home)))

(defroutes home-routes
  (GET "/" [] (home)))