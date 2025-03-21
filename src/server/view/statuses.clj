(ns server.view.statuses
  (:require
   [hiccup2.core :refer [html]]
   [hiccup.element :refer [link-to]]
   [hiccup.form :as form]

   [server.view.layout :as layout]
   [server.view.mixins :refer [table-render]])
  (:gen-class))

(def data {:statuses [:id :name :date :action]})

(defn statuses-page
  ([request] (statuses-page request []))
  ([request content] (layout/common
                      request
                      (let [i18n (get-in request [:translations :tables])]
                        (html
                         [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :statuses] "Default")]
                         (link-to {:class "btn btn-primary"}
                                  (format "/%s/new" (-> data keys first name))
                                  (get-in request [:translations :statuses :new] "Default"))
                         (table-render data i18n content))))))

(defn statuses-new
  [request {:keys [errors values]}]
  (layout/common
   request
   (html
    [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :statuses-new] "Default")]
    (form/form-to [:post "/statuses"]
                  [:div.form-floating.mb-3
                   [:input#data-name.form-control {:name "name"
                                                   :placeholder (get-in request [:translations :form :name] "Default")
                                                   :type "text"
                                                   :value (:name values)
                                                   :class (if (contains? errors :name)
                                                            "form-control is-invalid"
                                                            "form-control")}]
                   (when (:name errors) [:div.form-control-feedback.invalid-feedback (:name errors)])
                   (form/label {:for "data-name"} :name (get-in request [:translations :form :name] "Default"))]
                  (form/submit-button {:class "btn btn-primary"} (get-in request [:translations :form :btn-create] "Default"))))))

(defn statuses-edit
  [request {:keys [errors values]}]
  (layout/common
   request
   (html
    [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :statuses-edit] "Default")]
    (form/form-to [:post (format "/statuses/%s" (:id values))]
                  [:div.form-floating.mb-3
                   [:input#data-name.form-control {:name "name"
                                                   :placeholder (get-in request [:translations :form :name] "Default")
                                                   :type "text"
                                                   :value (:name values)
                                                   :class (if (contains? errors :name)
                                                            "form-control is-invalid"
                                                            "form-control")}]
                   (when (:name errors) [:div.form-control-feedback.invalid-feedback (:name errors)])
                   (form/label {:for "data-name"} :name (get-in request [:translations :form :name] "Default"))]
                  (form/hidden-field :_method :PATCH)
                  (form/submit-button {:class "btn btn-primary"} (get-in request [:translations :form :btn-change] "Default"))))))