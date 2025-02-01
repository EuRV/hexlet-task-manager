(ns server.view.session)

(defn login [{:keys [error]}]
  [:div.container.wrapper.flex-grow-1
   [:h1.display-4.fw-bold.mt-4 "Вход"]
   [:div.row.justify-content-center
    [:div.col-12.col-md-8
     [:div.card.shadow-sm
      [:div.card-body.row.p-5
       [:div.col-12.col-md-6.mt-3.t-mb-0
        [:form {:action "/session" :method "post"}
         [:div.form-floating.mb-3
          [:input {:id "data-email"
                   :name "email"
                   :placeholder "Email"
                   :type "text"
                   :value (if (seq error)
                            (:email error)
                            "")
                   :class (if (seq error)
                            "form-control is-invalid"
                            "form-control")}]
          (when (seq error) [:div.form-control-feedback.invalid-feedback (error :message)])
          [:label {:for "data-email"} "Email"]]
         [:div.form-floating.mb-3
          [:input.form-control {:id "data-password"
                                :name "password-digest"
                                :placeholder "Пароль"
                                :type "password"}]
          [:label {:for "data-password"} "Пароль"]]
         [:input.btn.btn-primary {:type "submit" :value "Войти"}]]]]]]]])