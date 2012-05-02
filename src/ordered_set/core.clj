(ns ordered-set.core)

(defn ordered-set [& items]
  (seq->ordered-set items))

(defn seq->ordered-set [items]
  (ordered_set.core.OrderedSet/create items))

(defmethod print-method ordered_set.core.OrderedSet [o ^java.io.Writer w]
  (.write w "#ordered-collection/set ")
  (print-method (seq o) w))