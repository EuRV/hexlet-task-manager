(ns server.view.tasks
  (:require
   [hiccup2.core :refer [html]]
   [hiccup.form :as form]
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

(defn task-page
  [request content]
  (layout/common
   request
   (let [task (first content)
         i18n (get-in request [:translations :tables])]
     (html
      [:h1.display-4.fw-bold.mt-4 (get task :task-name "Default")]
      [:div.row.mt-5.p-5.shadow.bg-white
       [:div.col-12.col-md-8.order-2.order-md-1
        [:div.lead.fw-normal.mb-4 (get task :description "Default")]]
       [:div.col-12.col-md-4.border-start.px-3.order-1.order-md-2.mb-3.mb-md-0
        [:div.mb-2
         [:span.me-1.badge.bg-danger.text-white (get task :status-name "Default")]]
        [:div.d-flex.flex-wrap.mb-3
         [:span.text-muted.me-2 (get-in request [:translations :tables :creator-name] "Default")]
         [:span (get task :creator-name "Default")]]
        [:div.d-flex.flex-wrap.mb-3
         [:span.text-muted.me-2 (get-in request [:translations :tables :executor-name] "Default")]
         [:span (get task :executor-name "Default")]]
        [:div.d-flex.flex-wrap.mb-3
         [:span.text-muted.me-2 (get-in request [:translations :tables :date] "Default")]
         [:span (get task :created-at "Default")]]
        [:div.d-flex.flex-wrap
         [:a.btn.btn-primary.me-1 {:href (format "tasks/%s/edit" (:id task))} (:btn-change i18n)]
         (form/form-to [:post (format "tasks/%s" (:id task))]
                       [:input {:type "hidden" :name "_method" :value "DELETE"}]
                       [:input.btn.btn-danger {:type "submit" :value (:btn-delete i18n)}])]]]))))