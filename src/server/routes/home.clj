(ns server.routes.home
  (:require [compojure.core :refer [GET defroutes]]

            [server.view.layout :as layout]
            [server.view.home :as view])
  (:gen-class))

(defroutes home-routes
  (GET "/" {:keys [session]} (layout/common session (view/home))))