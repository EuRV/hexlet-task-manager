(ns server.view.home
  (:gen-class))

(defn home 
  []
  [:div.container
   [:div.row
    [:div.col-12
     [:div.card.shadow.bg-white.rounded-3
      [:div.card-body.p-5
       [:div.display-4.fw-bold.mb-0 "Привет от Хекслета!"]
       [:p.lead "Практические курсы по программированию"]
       [:a.btn.btn-primary.btn-lg.px-4.mt-3.fw-bold {:href "https://hexlet.io" :target "_blank"} "Узнать Больше"]]]]]])