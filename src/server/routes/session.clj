(ns server.routes.session
  (:require
   [compojure.core :refer [defroutes GET POST DELETE]]
   [server.view.layout :as layout]
   [server.view.session :as view])
  (:gen-class))

(defroutes session-routes
  (GET "/session/new" [] (layout/common (view/login)))
  (POST "/session" [] ())
  (DELETE "/session" [] ()))