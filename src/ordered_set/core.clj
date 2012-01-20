(ns ordered-set.core)

(defn ordered-set [& items]
  (ordered_set.core.OrderedSet/create items))
