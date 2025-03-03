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

(defn clean-task-data
  [task]
  (reduce-kv (fn [acc k v]
               (cond
                 (and (k #{:description :executor-id}) (= v "")) acc
                 (k #{:name :description}) (assoc acc k v)
                 (k #{:status-id :creator-id :executor-id}) (assoc acc k (to-number v))
                 :else acc))
             {}
             task))