(ns ordered-map.core)

(defn ordered-map [& kvs]
  (entries->ordered-map (map vec (partition-all 2 kvs))))

(defn entries->ordered-map [kvs]
  (into (ordered_map.core.OrderedMap/EMPTY)
        (seq kvs)))

(defmethod print-method ordered_map.core.OrderedMap [o ^java.io.Writer w]
  (.write w "#ordered-collection/map ")
  (print-method (seq o) w))