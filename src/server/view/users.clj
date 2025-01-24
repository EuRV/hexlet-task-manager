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