(ns server.middleware
  (:require
   [ring.util.response :as resp]
   [clojure.string :as string]
   [server.locales :refer [locales]])
  (:gen-class))

(defn wrap-i18n [handler]
  (fn [request]
    (let [accept-language (get-in request [:headers "accept-language"] "ru")
          lang (keyword (or (first (string/split accept-language #"-|,")) "ru"))
          translations (get locales lang {})]
      (handler (assoc request :translations translations)))))

(defn wrap-protected
  ([handler] (wrap-protected handler "/"))
  ([handler redirect-url]
   (fn [request]
     (if (get-in request [:session :user-id])
       (handler request)
       (-> (resp/redirect redirect-url)
           (assoc :flash {:type "danger" :message "Доступ запрещён! Пожалуйста, авторизируйтесь."}))))))