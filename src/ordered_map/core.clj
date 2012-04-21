(ns ordered-map.core)

(defn ordered-map [& kvs]
  (reduce (fn [m [k v]]
            (assoc m k v))
          (ordered_map.core.OrderedMap/EMPTY)
          (partition-all 2 kvs)))
