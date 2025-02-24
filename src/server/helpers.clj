(ns server.helpers)

(defn to-number [value]
  (cond
    (number? value) value
    (string? value) (try
                      (Long/parseLong value)
                      (catch Exception _
                        nil))
    :else nil))

(defn clean-data
  [data template]
  (reduce-kv (fn [init key value]
               (if (key template) (assoc init key value) init))
             {}
             data))