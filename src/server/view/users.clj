(ns server.view.users)

(defn users
  []
  [:div.table-responsive
   [:table.table.table-borderless.table-striped.mt-5.bg-white
    [:thead
     [:tr
      [:th "ID"]
      [:th "Полное имя"]
      [:th "Email"]
      [:th "Дата создания"]
      [:th "Действия"]]]
    [:tbody]]])