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

(defn users-new []
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
                                :type "text"}]
          [:label {:for "data-first-name"} "Имя"]]
         [:div.form-floating.mb-3
          [:input.form-control {:id "data-last-name"
                                :name "last-name"
                                :placeholder "Фамилия"
                                :type "text"}]
          [:label {:for "data-last-name"} "Фамилия"]]
         [:div.form-floating.mb-3
          [:input.form-control {:id "data-email"
                                :name "email"
                                :placeholder "Email"
                                :type "text"}]
          [:label {:for "data-email"} "Email"]]
         [:div.form-floating.mb-3
          [:input.form-control {:id "data-password"
                                :name "password-digest"
                                :placeholder "Пароль"
                                :type "password"}]
          [:label {:for "data-password"} "Пароль"]]
         [:input.btn.btn-primary {:type "submit" :value "Сохранить"}]]]]]]]])