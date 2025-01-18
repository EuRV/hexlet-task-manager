(ns server.view.layout
  (:require [hiccup2.core :as h]))
  
  (defn common
    [& content]
    (str
     (h/html (h/raw "<!DOCTYPE html>")
             [:html {:lang "ru"}
              [:head
               [:meta {:charset "UTF-8"}]
               [:meta {:name "viewport" :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
               [:title "Hexlet Task Manager"]
               [:link {:href "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css", :rel "stylesheet", :integrity "sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH", :crossorigin "anonymous"}]]
              [:body.d-flex.flex-column.min-vh-100.bg-light
               [:nav.navbar.navbar-expand-lg.navbar-light.bg-white
                [:div.container
                 [:a.navbar-brand {:href "/"} "Менеджер задач"]
                 [:button.navbar-toggler {:data-bs-toggle "collapse" :data-bs-target "#navbarToggleExternalContent"}
                  [:span.navbar-toggler-icon]]
                 [:div#navbarToggleExternalContent.collapse.navbar-collapse
                  [:ul.navbar-nav.justify-content-end.w-100
                   [:li.nav-item.me-auto
                    [:a.nav-link {:href "/users"} "Пользователи"]]
                   [:li.nav-item
                    [:a.nav-link {:href "/session/new"} "Вход"]]
                   [:li.nav-item
                    [:a.nav-link {:href "/users/new"} "Регистрация"]]]]]]
               [:div.container.wrapper.flex-grow-1
                [:h1.display-4.fw-bold.mt-4]
                content]
               [:footer.bg-dark.text-light
                [:div.container.py-3
                 [:p.lead.mb-0 "© Hexlet Ltd, 2021"]]]
               [:script {:src "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" :integrity "sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" :crossorigin "anonymous"}]]])))