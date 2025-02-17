(ns server.helpers)

(defn formatter-date [date]
  (.format (java.text.SimpleDateFormat. "MM/dd/yyyy HH:mm:ss") date))

(defn formatter-fname [fname lname]
  (format "%s %s" fname lname))

(defn formatter-users [user]
  (let [{:keys [id first-name last-name email created-at]} user]
    {:id id
     :fname (formatter-fname first-name last-name)
     :email email
     :date (formatter-date created-at)}))

(defn to-number [value]
  (cond
    (number? value) value
    (string? value) (try
                      (Long/parseLong value)
                      (catch Exception _
                        nil))
    :else nil))

(defn clean-data
  [data]
  (reduce-kv (fn [init key value]
               (if (key #{:first-name :last-name :email :password-digest}) (assoc init key value) init))
             {}
             data))