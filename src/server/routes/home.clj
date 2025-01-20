(ns server.routes.home
  (:require [compojure.core :refer [GET defroutes]]

            [server.view.layout :as layout]
            [server.view.home :as view])
  (:gen-class))

(def content {:page-name ""
              :page (view/home)})

(defn home
  []
  (layout/common content))

(defroutes home-routes
  (GET "/" [] (home)))