(ns server.view.users)

(defn users
  [content]
  [:div.table-responsive
   [:table.table.table-borderless.table-striped.mt-5.bg-white
    [:thead
     [:tr
      [:th "ID"]
      [:th "Полное имя"]
      [:th "Email"]
      [:th "Дата создания"]
      [:th "Действия"]]]
    [:tbody
     (for [[id fname email date] content]
       [:tr
        [:td id]
        [:td fname]
        [:td email]
        [:td date]
        [:td
         [:div.d-flex.flex-wrap
          [:a.btn.btn-primary.me-1 {:href (format "users/%s/edit" id)} "Изменить"]
          [:form {:action (format "users/%s" id) :metod "post"}
           [:input {:name "_metod" :type "hidden" :value "delete"}]
           [:input.btn.btn-danger {:type "submit" :value "Удалить"}]]]]])]]])