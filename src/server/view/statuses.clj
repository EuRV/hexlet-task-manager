(ns server.view.statuses
  (:require
   [hiccup2.core :refer [html]]
   [hiccup.element :refer [link-to]]

   [server.view.layout :as layout]
   [server.view.mixins :refer [table-render]])
  (:gen-class))

(def data {:statuses [:id :name :date :action]})

(defn statuses-page
  [request content]
  (layout/common
   request
   (let [i18n (get-in request [:translations :tables])]
     (html
      [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :statuses] "Default")]
      (link-to {:class "btn btn-primary"}
               (format "/%s/new" (:entity data))
               (get-in request [:translations :statuses :new] "Default"))
      (table-render data i18n content)))))