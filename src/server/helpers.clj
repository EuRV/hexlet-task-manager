(ns server.helpers
  (:require [clojure.spec.alpha :as s]))

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
                 (k #{:labels}) (assoc acc k (mapv to-number (flatten [v])))
                 :else acc))
             {}
             task))

(defn validate-data
  [entity spec-errors]
  (fn [data]
    (if (s/valid? entity data)
      {:valid? true :errors {} :values data}
      {:valid? false
       :errors (->> (s/explain-data entity data)
                    :clojure.spec.alpha/problems
                    (reduce (fn
                              [init problem]
                              (assoc init (-> problem :in last) (get spec-errors (-> problem :via peek))))
                            {}))
       :values (->> (s/explain-data entity data)
                    :clojure.spec.alpha/value)})))