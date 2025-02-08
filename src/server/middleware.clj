(ns server.middleware
  (:require
   [clojure.string :as string]
   [server.locales :refer [locales]])
  (:gen-class))

(defn wrap-i18n [handler]
  (fn [request]
    (let [accept-language (get-in request [:headers "accept-language"] "ru")
          lang (keyword (or (first (string/split accept-language #"-|,")) "ru"))
          translations (get locales lang {})]
      (handler (assoc request :translations translations)))))


(comment
  (get locales :ru {})
  :rcf)