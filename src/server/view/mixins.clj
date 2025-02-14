(ns server.view.mixins
  (:require [hiccup.form :as form])
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

(defn table-render
  [data i18n contents]
  (let [entity (-> data keys first name)
        headings (-> data vals first)]
    [:div.table-responsive
     [:table.table.table-borderless.table-striped.mt-5.bg-white
      [:thead
       (table-head headings i18n)]
      [:tbody
       (table-body entity contents i18n)]]]))