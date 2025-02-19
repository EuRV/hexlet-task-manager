(ns server.view.tasks
  (:require
   [hiccup2.core :refer [html]]
   [hiccup.element :refer [link-to]]
      
   [server.view.layout :as layout]
   [server.view.mixins :refer [table-render]])
  (:gen-class))

(def data {:tasks [:id :name :status-name :creator-name :executor-name :date :action]})

(defn tasks-page
  [request content]
  (layout/common
   request
   (let [i18n (get-in request [:translations :tables])]
     (html
      [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :tasks] "Default")]
      (link-to {:class "btn btn-primary"}
               (format "/%s/new" (-> data keys first name))
               (get-in request [:translations :tasks :new] "Default"))
      (table-render data i18n content)))))