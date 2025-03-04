(ns server.view.labels
  (:require
   [hiccup2.core :refer [html]]
   [hiccup.element :refer [link-to]]
  
   [server.view.layout :as layout]
   [server.view.mixins :refer [table-render]])
  (:gen-class))

(def data {:labels [:id :name :date :action]})

(defn labels-page
  [request content]
  (layout/common
   request
   (let [i18n (get-in request [:translations :tables])]
     (html
      [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :labels] "Default")]
      (link-to {:class "btn btn-primary mb-5"}
               (format "/%s/new" (-> data keys first name))
               (get-in request [:translations :labels :new] "Default"))
      (table-render data i18n content)))))