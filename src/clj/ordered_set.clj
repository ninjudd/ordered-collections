(ns ordered-set
  (:import [clojure.lang PersistentOrderedSet]))

(defn ordered-set [& items]
  (clojure.lang.PersistentOrderedSet/create items))
