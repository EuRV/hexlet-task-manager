(ns server.view.home
  (:require
   [hiccup2.core :refer [html]]
   [server.view.layout :as layout])
  (:gen-class))

(defn home
  [request]
  (layout/common
   request
   (html [:h1.display-4.fw-bold.mt-4 ""]
         [:div.container
          [:div.row
           [:div.col-12
            [:div.card.shadow.bg-white.rounded-3
             [:div.card-body.p-5
              [:div.display-4.fw-bold.mb-0 (get-in request [:translations :home :welcome] "")]
              [:p.lead "Практические курсы по программированию"]
              [:a.btn.btn-primary.btn-lg.px-4.mt-3.fw-bold {:href "https://hexlet.io" :target "_blank"} "Узнать Больше"]]]]]])))