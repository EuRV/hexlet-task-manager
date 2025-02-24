(ns server.view.mixins
  (:require
   [hiccup.form :as form]
   [hiccup.element :refer [link-to]])
  (:gen-class))

(defn table-action [entity id i18n]
  [:td
   [:div.d-flex.flex-wrap
    [:a.btn.btn-primary.me-1 {:href (format "%s/%s/edit" entity id)} (:btn-change i18n)]
    (form/form-to [:post (format "%s/%s" entity id)]
                  [:input {:type "hidden" :name "_method" :value "DELETE"}]
                  [:input.btn.btn-danger {:type "submit" :value (:btn-delete i18n)}])]])

(defn table-head [keys i18n]
  (into [] (reduce #(conj % (vector :th %2)) [:tr] (for [key keys
                                                         :let [val (key i18n)]]
                                                     val))))

(defn table-body [entity contents i18n]
  (for [{:keys [id] :as item} contents
        :let [new-item (reduce #(conj % (vector :td %2)) [:tr] (vals item))]]
    (into [] (concat new-item [(table-action entity id i18n)]))))

(defn table-tasks-body [entity contents i18n]
  (for [{:keys [id] :as item} contents
        :let [new-item (reduce-kv #(conj % (if (= %2 :task-name)
                                             (vector :td (link-to (format "/tasks/%s" id) %3))
                                             (vector :td %3)))
                                  [:tr] item)]]
    (into [] (concat new-item [(table-action entity id i18n)]))))

(defn table-render
  [data i18n contents]
  (let [entity (-> data keys first name)
        headings (-> data vals first)]
    [:div.table-responsive
     [:table.table.table-borderless.table-striped.mt-5.bg-white
      [:thead
       (table-head headings i18n)]
      [:tbody
       (if (contains? data :tasks)
         (table-tasks-body entity contents i18n)
         (table-body entity contents i18n))]]]))

(comment
  (table-render
   {:tasks [:id :name :status-name :creator-name :executor-name :date :action]}
   {:action "Действия"
    :btn-change "Изменить"
    :btn-delete "Удалить"
    :creator-name "Автор"
    :date "Дата создания"
    :email "Email"
    :executor-name "Исполнитель"
    :fname "Полное имя"
    :id "ID"
    :name "Наименование"
    :status-name "Статус"}
   [{:id 1,
     :task-name "go",
     :status-name "Started",
     :creator-name "Artem Ivanov",
     :executor-name "Pavel Socolov",
     :created-at #inst "2025-02-19T08:35:48.872876000-00:00"}])
  :rcf)