(ns hotreload
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [server.core :refer [app]])
  (:gen-class))

(def dev-handler
  (wrap-reload #'app))

#_{:clj-kondo/ignore [:unused-binding]}
(defn -main [& args]
  (run-jetty #'dev-handler {:port 8080}))