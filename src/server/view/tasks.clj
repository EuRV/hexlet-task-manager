(ns server.view.tasks
  (:require
   [hiccup2.core :refer [html]]
   [hiccup.form :as form]
   [hiccup.element :refer [link-to]]
      
   [server.view.layout :as layout]
   [server.view.mixins :refer [table-render]]
   [server.helpers :as h])
  (:gen-class))

(def data {:tasks [:id :name :status-name :creator-name :executor-name :date :action]})

(defn tasks-page
  [request content statuses users labels]
  (layout/common
   request
   (let [i18n (get-in request [:translations :tables])]
     (html
      [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :tasks] "Default")]
      (link-to {:class "btn btn-primary mb-5"}
               (format "/%s/new" (-> data keys first name))
               (get-in request [:translations :tasks :new] "Default"))
      [:div.card.shadow-sm
       [:div.card-body.p-4
        (form/form-to [:get "/tasks"]
                      [:div.row
                       [:div.col-12.col-md
                        [:div.input-group.mb-3
                         (form/label {:class "input-group-text"
                                      :for "data-status"}
                                     :status
                                     (get-in request [:translations :form :status] "Default"))
                         (reduce #(conj % (vector :option (assoc {} :value (:id %2) :selected (= (str (:id %2)) (-> request :params :status))) (:name %2)))
                                 [:select#data-status.form-select {:name "status"}
                                  [:option]]
                                 statuses)]]
                       [:div.col-12.col-md
                        [:div.input-group.mb-3
                         (form/label {:class "input-group-text"
                                      :for "data-executor"}
                                     :executor (get-in request [:translations :form :executor] "Default"))
                         (reduce #(conj % (vector :option (assoc {} :value (:id %2) :selected (= (str (:id %2)) (-> request :params :executor))) (:fname %2)))
                                 [:select#data-executor.form-select {:name "executor"}
                                  [:option]]
                                 users)]]
                       [:div.col-12.col-md
                        [:div.input-group.mb-3
                         (form/label {:class "input-group-text"
                                      :for "data-label"}
                                     :label (get-in request [:translations :form :label] "Default"))
                         (reduce #(conj % (vector :option (assoc {} :value (:id %2) :selected (= (str (:id %2)) (-> request :params :label))) (:name %2)))
                                 [:select#data-label.form-select {:name "label"}
                                  [:option]]
                                 labels)]]]
                      [:div.mb-3.form-check
                       [:input#data-is-creator-user.form-check-input {:type "checkbox"
                                                                      :name "is-creator-user"
                                                                      :checked (-> request :params :is-creator-user)}]
                       (form/label {:class "form-check-label"
                                    :for "data-is-creator-user"}
                                   :my-tasks
                                   (get-in request [:translations :form :my-tasks] "Default"))]
                      (form/submit-button {:class "btn btn-primary"} (get-in request [:translations :form :btn-show] "Default")))]]
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
        (if (seq (:labels task))
          (reduce #(conj % [:span.me-1.badge.bg-info.text-white %2])
                  [:div.mb-2
                   [:span.me-1.badge.bg-danger.text-white (get task :status-name "Default")]]
                  (:labels task))
          [:div.mb-2
           [:span.me-1.badge.bg-danger.text-white (get task :status-name "Default")]])
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
         [:a.btn.btn-primary.me-1 {:href (format "/tasks/%s/edit" (:id task))} (:btn-change i18n)]
         (form/form-to [:post (format "/tasks/%s" (:id task))]
                       [:input {:type "hidden" :name "_method" :value "DELETE"}]
                       [:input.btn.btn-danger {:type "submit" :value (:btn-delete i18n)}])]]]))))

(defn select-statuses
  [errors]
  [:select {:class (if (contains? errors :status-id)
                     "form-control is-invalid"
                     "form-control")
            :id "data-status-id"
            :name "status-id"}
   [:option]])

(def select-users
  [:select {:class "form-control"
            :id "data-executor-id"
            :name "executor-id"}
   [:option]])

(def select-labels
  [:select {:class "form-control"
            :id "data-labels"
            :name "labels"
            :multiple "multiple"}])

(defn task-new
  [request {:keys [errors values]} statuses users labels]
  (layout/common
   request
   (html
    [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :task-new] "Default")]
    (form/form-to [:post "/tasks"]
                  [:div.form-floating.mb-3
                   [:input#data-name {:name "name"
                                      :placeholder (get-in request [:translations :form :name] "Default")
                                      :type "text"
                                      :value (:name values)
                                      :class (if (contains? errors :name)
                                               "form-control is-invalid"
                                               "form-control")}]
                   (when (:name errors) [:div.form-control-feedback.invalid-feedback (:name errors)])
                   (form/label {:for "data-name"} :name (get-in request [:translations :form :name] "Default"))]
                  [:div.mb-3
                   (form/label {:for "data-description"} :description (get-in request [:translations :form :description] "Default"))
                   (form/text-area {:class "form-control"
                                    :id "data-description"
                                    :rows "3"}
                                   :description
                                   (get values :description))]
                  [:div.mb-3
                   (form/label {:for "data-status-id"} :status-id (get-in request [:translations :form :status] "Default"))
                   (reduce #(conj % (vector :option (assoc {} :value (:id %2) :selected (= (:id %2) (get values :status-id))) (:name %2))) (select-statuses errors) statuses)
                   (when (:status-id errors) [:div.form-control-feedback.invalid-feedback (:status-id errors)])]
                  [:div.mb-3
                   (form/label {:for "data-executor-id"} :executor-id (get-in request [:translations :form :executor] "Default"))
                   (reduce #(conj % (vector :option (assoc {} :value (:id %2) :selected (= (:id %2) (get values :executor-id))) (:fname %2))) select-users users)]
                  [:div.mb-3
                   (form/label {:for "data-labels"} :labels (get-in request [:translations :form :labels] "Default"))
                   (reduce #(conj % (vector :option (assoc {} :value (:id %2)) (:name %2))) select-labels labels)]
                  (form/submit-button {:class "btn btn-primary"} (get-in request [:translations :form :btn-create] "Default"))))))

(defn task-edit
  [request {:keys [errors values]} statuses users labels]
  (layout/common
   request
   (html
    [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :task-edit] "Default")]
    (form/form-to [:post (format "/tasks/%s" (-> request :params :id h/to-number))]
                  [:div.form-floating.mb-3
                   [:input#data-name {:name "name"
                                      :placeholder (get-in request [:translations :form :name] "Default")
                                      :type "text"
                                      :value (:name values)
                                      :class (if (contains? errors :name)
                                               "form-control is-invalid"
                                               "form-control")}]
                   (when (:name errors) [:div.form-control-feedback.invalid-feedback (:name errors)])
                   (form/label {:for "data-name"} :name (get-in request [:translations :form :name] "Default"))]
                  [:div.mb-3
                   (form/label {:for "data-description"} :description (get-in request [:translations :form :description] "Default"))
                   (form/text-area {:class "form-control"
                                    :id "data-description"
                                    :rows "3"}
                                   :description
                                   (get values :description))]
                  [:div.mb-3
                   (form/label {:for "data-status-id"} :status-id (get-in request [:translations :form :status] "Default"))
                   (reduce #(conj % (vector :option (assoc {} :value (:id %2) :selected (= (:id %2) (get values :status-id))) (:name %2))) (select-statuses errors) statuses)
                   (when (:status-id errors) [:div.form-control-feedback.invalid-feedback (:status-id errors)])]
                  [:div.mb-3
                   (form/label {:for "data-executor-id"} :executor-id (get-in request [:translations :form :executor] "Default"))
                   (reduce #(conj % (vector :option (assoc {} :value (:id %2) :selected (= (:id %2) (get values :executor-id))) (:fname %2))) select-users users)]
                  [:div.mb-3
                   (form/label {:for "data-labels"} :labels (get-in request [:translations :form :labels] "Default"))
                   (reduce #(conj % (vector :option (assoc {} :value (:id %2) :selected (= (:id %2) (get values :label-id))) (:name %2))) select-labels labels)]
                  (form/hidden-field :_method :PATCH)
                  (form/submit-button {:class "btn btn-primary"} (get-in request [:translations :form :btn-change] "Default"))))))