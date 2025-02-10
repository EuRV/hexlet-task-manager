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
  [t]
  [:ul.navbar-nav.justify-content-end.w-100
   [:li.nav-item.me-auto
    [:a.nav-link {:href "/users"} (-> t :layout :users)]]
   [:li.nav-item
    [:a.nav-link {:href "/session/new"} (-> t :layout :session-new)]]
   [:li.nav-item
    [:a.nav-link {:href "/users/new"} (-> t :layout :users-new)]]])

(defn user-nav
  [t]
  [:ul.navbar-nav.justify-content-end.w-100
   [:li.nav-item.me-auto
    [:a.nav-link {:href "/users"} (-> t :layout :users)]]
   [:li.nav-item
    [:a.nav-link {:href "/statuses"} (-> t :layout :statuses)]]
   [:li.nav-item
    [:a.nav-link {:href "/labels"} (-> t :layout :labels)]]
   [:li.nav-item
    [:a.nav-link {:href "/tasks"} (-> t :layout :tasks)]]
   [:li.nav-item
    [:form {:action "/session" :method "post"}
     [:input {:name "_method" :type "hidden" :value "delete"}]
     [:input.btn.nav-link {:type "submit" :value (-> t :layout :signOut)}]]]])

(defn common [request header page]
  (let [t (:translations request)
        session (:session request)
        flash (:flash request)]
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
                  (if (seq session)
                    (user-nav t)
                    (guest-nav t))]]]
               [:div.container.wrapper.flex-grow-1
                (when flash (render-flash flash))
                [:h1.display-4.fw-bold.mt-4 (get-in t [:layout header] "")]
                page]
               [:footer.bg-dark.text-light
                [:div.container.py-3
                 [:p.lead.mb-0 "© Hexlet Ltd, 2021"]]]]]))))