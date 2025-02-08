(ns server.locales
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn])
  (:gen-class))

(defn load-locale [lang]
  (let [resource (io/resource (str "locales/" lang ".edn"))]
    (if resource
      (-> resource slurp edn/read-string)
      {})))

(def locales
  {:ru (load-locale "ru")})