(ns server.view.users
  (:require
   [hiccup.form :as form]
   [server.view.mixins :refer [table-render]])
  (:gen-class))

(def data {:entity "users"
           :headings {:id "ID"
                      :fname "Полное имя"
                      :email "Email"
                      :date "Дата создания"
                      :action "Действия"}})

(defn users-page [content]
  (#(table-render data %) content))

(defn users-new [{:keys [errors values]}]
  [:div.row.justify-content-center
   [:div.col-12.col-md-8
    [:div.card.shadow-sm
     [:div.card-body.row.p-5
      [:div.col-12.col-md-6.mt-3.t-mb-0
       [:form {:action "/users" :method "post"}
        [:div.form-floating.mb-3
         [:input.form-control {:id "data-first-name"
                               :name "first-name"
                               :placeholder "Имя"
                               :type "text"
                               :value (:first-name values)
                               :class (if (contains? errors :first-name)
                                        "form-control is-invalid"
                                        "form-control")}]
         (when (:first-name errors) [:div.form-control-feedback.invalid-feedback (:first-name errors)])
         [:label {:for "data-first-name"} "Имя"]]
        [:div.form-floating.mb-3
         [:input.form-control {:id "data-last-name"
                               :name "last-name"
                               :placeholder "Фамилия"
                               :type "text"
                               :value (:last-name values)
                               :class (if (contains? errors :last-name)
                                        "form-control is-invalid"
                                        "form-control")}]
         (when (:last-name errors) [:div.form-control-feedback.invalid-feedback (:last-name errors)])
         [:label {:for "data-last-name"} "Фамилия"]]
        [:div.form-floating.mb-3
         [:input.form-control {:id "data-email"
                               :name "email"
                               :placeholder "Email"
                               :type "text"
                               :value (:email values)
                               :class (if (contains? errors :email)
                                        "form-control is-invalid"
                                        "form-control")}]
         (when (:email errors) [:div.form-control-feedback.invalid-feedback (:email errors)])
         [:label {:for "data-email"} "Email"]]
        [:div.form-floating.mb-3
         [:input.form-control {:id "data-password"
                               :name "password-digest"
                               :placeholder "Пароль"
                               :type "password"
                               :value (:password-digest values)
                               :class (if (contains? errors :password-digest)
                                        "form-control is-invalid"
                                        "form-control")}]
         (when (:password-digest errors) [:div.form-control-feedback.invalid-feedback (:password-digest errors)])
         [:label {:for "data-password"} "Пароль"]]
        [:input.btn.btn-primary {:type "submit" :value "Сохранить"}]]]]]]])

(defn users-edit [{:keys [errors values]}]
  (form/form-to [:post (format "/users/%s" (:id values))]
                [:div.form-floating.mb-3
                 [:input#data-first-name.form-control {:name "first-name"
                                                       :placeholder "Имя"
                                                       :type "text"
                                                       :value (:first-name values)
                                                       :class (if (contains? errors :first-name)
                                                                "form-control is-invalid"
                                                                "form-control")}]
                 (when (:first-name errors) [:div.form-control-feedback.invalid-feedback (:first-name errors)])
                 (form/label {:for "data-first-name"} :first-name "Имя")]
                [:div.form-floating.mb-3
                 [:input#data-last-name.form-control {:name "last-name"
                                                      :placeholder "Фамилия"
                                                      :type "text"
                                                      :value (:last-name values)
                                                      :class (if (contains? errors :last-name)
                                                               "form-control is-invalid"
                                                               "form-control")}]
                 (when (:last-name errors) [:div.form-control-feedback.invalid-feedback (:last-name errors)])
                 [:label {:for "data-last-name"} "Фамилия"]]
                [:div.form-floating.mb-3
                 [:input#data-email.form-control {:name "email"
                                                  :placeholder "Email"
                                                  :type "text"
                                                  :value (:email values)
                                                  :class (if (contains? errors :email)
                                                           "form-control is-invalid"
                                                           "form-control")}]
                 (when (:email errors) [:div.form-control-feedback.invalid-feedback (:email errors)])
                 [:label {:for "data-email"} "Email"]]
                [:div.form-floating.mb-3
                 [:input#data-password.form-control {:name "password-digest"
                                                     :placeholder "Пароль"
                                                     :type "password"
                                                     :value (:password-digest values)
                                                     :class (if (contains? errors :password-digest)
                                                              "form-control is-invalid"
                                                              "form-control")}]
                 (when (:password-digest errors) [:div.form-control-feedback.invalid-feedback (:password-digest errors)])
                 [:label {:for "data-password"} "Пароль"]]
                (form/hidden-field :_method :PATCH)
                (form/submit-button {:class "btn btn-primary"} "Изменить")))

(def template
  {:first-name {:placeholder "Имя"
                :value ""
                :error nil}
   :last-name {:placeholder "Фамилия"
               :value ""
               :error nil}
   :email {:placeholder "Email"
           :value ""
           :error nil}
   :password-digest {:placeholder "Пароль"
                     :value ""
                     :error nil}})
(defn get-form [key data]
  [:div.form-floating.mb-3
   [:input.form-control {:id (format "data-%s" (name key))
                         :name (name key)
                         :placeholder (:placeholder data)
                         :type (if (= (name key) "password-digest") "password" "text")
                         :value (:value data)
                         :class (if (:error data)
                                  "form-control is-invalid"
                                  "form-control")}]
   (when (:error data) [:div.form-control-feedback.invalid-feedback (:error data)])
   [:label {:for (format "data-%s" (name key))} (:placeholder data)]])
(defn in-out-structure
  [out opts in]
  (reduce-kv (fn [init key val]
               (when (contains? init key) (assoc-in init [key opts] val)))
             in
             out))

(defn new-users-edit [{:keys [errors values]}]
  [:div.container.wrapper.flex-grow-1
   [:h1.display-4.fw-bold.mt-4 "Изменение пользователя"]
   [:form {:method "post" :action (format "/users/%s" (:id values))}
    (->>
     template
     (in-out-structure (dissoc values :id) :value)
     (in-out-structure errors :error)
     (mapv #(get-form (first %) (second %))))
    (form/hidden-field :_method :PATCH)
    (form/submit-button {:class "btn btn-primary"} "Изменить")]])

(comment
  (def values {:id 39 :first-name "Artem", :last-name "Ivanov", :email "a_ivanov@test.com", :password-digest "asdfg"})
  (def errors {:email "Не соответствует формату email"}) 

  (new-users-edit {:errors errors :values values })
  :rcf)