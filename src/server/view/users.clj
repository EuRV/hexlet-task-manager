(ns server.view.users
  (:require
   [hiccup2.core :refer [html]]
   [hiccup.form :as form]

   [server.view.layout :as layout]
   [server.view.mixins :refer [table-render]])
  (:gen-class))

(def data {:users [:id :fname :email :date :action]})

(defn users-page
  ([request] (users-page request []))
  ([request content] (layout/common
                      request
                      (let [i18n (get-in request [:translations :tables])]
                        (html
                         [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :users] "Default")]
                         (table-render data i18n content))))))

(defn users-new [request {:keys [errors values]}]
  (layout/common
   request
   (html
    [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :users-new] "Default")]
    [:div.row.justify-content-center
     [:div.col-12.col-md-8
      [:div.card.shadow-sm
       [:div.card-body.row.p-5
        [:div.col-12.col-md-6.mt-3.t-mb-0
         [:form {:action "/users" :method "post"}
          [:div.form-floating.mb-3
           [:input.form-control {:id "data-first-name"
                                 :name "first-name"
                                 :placeholder (get-in request [:translations :form :first-name] "Default")
                                 :type "text"
                                 :value (:first-name values)
                                 :class (if (contains? errors :first-name)
                                          "form-control is-invalid"
                                          "form-control")}]
           (when (:first-name errors) [:div.form-control-feedback.invalid-feedback (:first-name errors)])
           [:label {:for "data-first-name"} (get-in request [:translations :form :first-name] "Default")]]
          [:div.form-floating.mb-3
           [:input.form-control {:id "data-last-name"
                                 :name "last-name"
                                 :placeholder (get-in request [:translations :form :last-name] "Default")
                                 :type "text"
                                 :value (:last-name values)
                                 :class (if (contains? errors :last-name)
                                          "form-control is-invalid"
                                          "form-control")}]
           (when (:last-name errors) [:div.form-control-feedback.invalid-feedback (:last-name errors)])
           [:label {:for "data-last-name"} (get-in request [:translations :form :last-name] "Default")]]
          [:div.form-floating.mb-3
           [:input.form-control {:id "data-email"
                                 :name "email"
                                 :placeholder (get-in request [:translations :form :email] "Default")
                                 :type "text"
                                 :value (:email values)
                                 :class (if (contains? errors :email)
                                          "form-control is-invalid"
                                          "form-control")}]
           (when (:email errors) [:div.form-control-feedback.invalid-feedback (:email errors)])
           [:label {:for "data-email"} (get-in request [:translations :form :email] "Default")]]
          [:div.form-floating.mb-3
           [:input.form-control {:id "data-password"
                                 :name "password-digest"
                                 :placeholder (get-in request [:translations :form :password-digest] "Default")
                                 :type "password"
                                 :value (:password-digest values)
                                 :class (if (contains? errors :password-digest)
                                          "form-control is-invalid"
                                          "form-control")}]
           (when (:password-digest errors) [:div.form-control-feedback.invalid-feedback (:password-digest errors)])
           [:label {:for "data-password"} (get-in request [:translations :form :password-digest] "Default")]]
          [:input.btn.btn-primary {:type "submit" :value (get-in request [:translations :form :btn-save] "Default")}]]]]]]])))

(defn users-edit [request {:keys [errors values]}]
  (layout/common
   request
   (html
    [:h1.display-4.fw-bold.mt-4 (get-in request [:translations :layout :users-edit] "Default")]
    (form/form-to [:post (format "/users/%s" (:id values))]
                  [:div.form-floating.mb-3
                   [:input#data-first-name.form-control {:name "first-name"
                                                         :placeholder (get-in request [:translations :form :first-name] "Default")
                                                         :type "text"
                                                         :value (:first-name values)
                                                         :class (if (contains? errors :first-name)
                                                                  "form-control is-invalid"
                                                                  "form-control")}]
                   (when (:first-name errors) [:div.form-control-feedback.invalid-feedback (:first-name errors)])
                   (form/label {:for "data-first-name"} :first-name (get-in request [:translations :form :first-name] "Default"))]
                  [:div.form-floating.mb-3
                   [:input#data-last-name.form-control {:name "last-name"
                                                        :placeholder (get-in request [:translations :form :last-name] "Default")
                                                        :type "text"
                                                        :value (:last-name values)
                                                        :class (if (contains? errors :last-name)
                                                                 "form-control is-invalid"
                                                                 "form-control")}]
                   (when (:last-name errors) [:div.form-control-feedback.invalid-feedback (:last-name errors)])
                   [:label {:for "data-last-name"} (get-in request [:translations :form :last-name] "Default")]]
                  [:div.form-floating.mb-3
                   [:input#data-email.form-control {:name "email"
                                                    :placeholder (get-in request [:translations :form :email] "Default")
                                                    :type "text"
                                                    :value (:email values)
                                                    :class (if (contains? errors :email)
                                                             "form-control is-invalid"
                                                             "form-control")}]
                   (when (:email errors) [:div.form-control-feedback.invalid-feedback (:email errors)])
                   [:label {:for "data-email"} (get-in request [:translations :form :email] "Default")]]
                  [:div.form-floating.mb-3
                   [:input#data-password.form-control {:name "password-digest"
                                                       :placeholder (get-in request [:translations :form :password-digest] "Default")
                                                       :type "password"
                                                       :value (:password-digest values)
                                                       :class (if (contains? errors :password-digest)
                                                                "form-control is-invalid"
                                                                "form-control")}]
                   (when (:password-digest errors) [:div.form-control-feedback.invalid-feedback (:password-digest errors)])
                   [:label {:for "data-password"} (get-in request [:translations :form :password-digest] "Default")]]
                  (form/hidden-field :_method :PATCH)
                  (form/submit-button {:class "btn btn-primary"} (get-in request [:translations :form :btn-change] "Default"))))))