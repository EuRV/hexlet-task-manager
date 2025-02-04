(ns server.view.users
  (:require [server.view.mixins :refer [table-render]])
  (:gen-class))

(def data {:entity "users"
           :headings {:id "ID"
                      :fname "Полное имя"
                      :email "Email"
                      :date "Дата создания"
                      :action "Действия"}})

(defn users-page [content]
  [:div.container.wrapper.flex-grow-1
   [:h1.display-4.fw-bold.mt-4 "Пользователи"]
   (#(table-render data %) content)])

(defn users-new [{:keys [errors values]}]
  [:div.container.wrapper.flex-grow-1
   [:h1.display-4.fw-bold.mt-4 "Регистрация"]
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
         [:input.btn.btn-primary {:type "submit" :value "Сохранить"}]]]]]]]])