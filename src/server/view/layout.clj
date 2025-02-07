(ns server.view.layout
  (:require [hiccup2.core :as h]
            [hiccup.page :refer [include-css include-js]])
  (:gen-class))

(defn render-flash [flash]
  (when-let [message (:message flash)]
    (let [type (:type flash)]
      [:div.alert {:class (str "alert-" type) :role "alert" :id "flash-message"}
       message])))

(defn guest-nav
  []
  [:ul.navbar-nav.justify-content-end.w-100
   [:li.nav-item.me-auto
    [:a.nav-link {:href "/users"} "Пользователи"]]
   [:li.nav-item
    [:a.nav-link {:href "/session/new"} "Вход"]]
   [:li.nav-item
    [:a.nav-link {:href "/users/new"} "Регистрация"]]])

(defn user-nav
  []
  [:ul.navbar-nav.justify-content-end.w-100
   [:li.nav-item.me-auto
    [:a.nav-link {:href "/users"} "Пользователи"]]
   [:li.nav-item
    [:a.nav-link {:href "/statuses"} "Статусы"]]
   [:li.nav-item
    [:a.nav-link {:href "/labels"} "Метки"]]
   [:li.nav-item
    [:a.nav-link {:href "/tasks"} "Задачи"]]
   [:li.nav-item
    [:form {:action "/session" :method "post"}
     [:input {:name "_method" :type "hidden" :value "delete"}]
     [:input.btn.nav-link {:type "submit" :value "Выход"}]]]])

(defn common [request page]
  (str
   (h/html (h/raw "<!DOCTYPE html>")
           [:html {:lang "ru"}
            [:head
             [:meta {:charset "UTF-8"}]
             [:meta {:name "viewport" :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
             [:title "Hexlet Task Manager"]
             (include-css "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css")
             (include-js "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js")]
            [:body.d-flex.flex-column.min-vh-100.bg-light
             [:nav.navbar.navbar-expand-lg.navbar-light.bg-white
              [:div.container
               [:a.navbar-brand {:href "/"} "Менеджер задач"]
               [:button.navbar-toggler {:data-bs-toggle "collapse" :data-bs-target "#navbarToggleExternalContent"}
                [:span.navbar-toggler-icon]]
               [:div#navbarToggleExternalContent.collapse.navbar-collapse
                (if (seq (:session request))
                  (user-nav)
                  (guest-nav))]]]
             [:div.container.wrapper.flex-grow-1
              (when-let [flash (:flash request)]
                (render-flash flash))
              [:h1.display-4.fw-bold.mt-4 ""]
              page]
             [:footer.bg-dark.text-light
              [:div.container.py-3
               [:p.lead.mb-0 "© Hexlet Ltd, 2021"]]]]])))