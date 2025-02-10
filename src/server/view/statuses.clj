(ns server.view.statuses
  (:require
   [server.view.mixins :refer [table-render]])
  (:gen-class))

(def data {:entity "statuses"
           :headings {:id "ID"
                      :name "Наименование"
                      :date "Дата создания"
                      :action "Действия"}})

(defn statuses-page
  [content]
  (#(table-render data %) content))