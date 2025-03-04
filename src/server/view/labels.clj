(ns server.view.labels
  (:require
   [hiccup2.core :refer [html]]
   [hiccup.element :refer [link-to]]
   [hiccup.form :as form]
  
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

(defn labels-new-page
  [request {:keys [errors values]}]
  (layout/common
   request
   (html
    [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :labels-new] "Default")]
    (form/form-to [:post "/labels"]
                  [:div.form-floating.mb-3
                   [:input#data-name.form-control {:name "name"
                                                   :placeholder (get-in request [:translations :form :name] "Default")
                                                   :type "text"
                                                   :value (:name values)
                                                   :class (when (contains? errors :name) "is-invalid")}]
                   (when (:name errors) [:div.form-control-feedback.invalid-feedback (:name errors)])
                   (form/label {:for "data-name"} :name (get-in request [:translations :form :name] "Default"))]
                  (form/submit-button {:class "btn btn-primary"} (get-in request [:translations :form :btn-create] "Default"))))))