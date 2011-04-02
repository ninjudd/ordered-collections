(ns ordered-set)

(defn ordered-set [& items]
  (clojure.lang.PersistentOrderedSet/create items))
