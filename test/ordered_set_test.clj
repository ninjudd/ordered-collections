(ns ordered-set-test
  (:use ordered-set)
  (:use clojure.test))

(deftest test-ordered-set
  (let [s (ordered-set 5 1 2 [:f])]
    (is (= '(5 1 2 [:f]) (seq s)))
    (is (= 4 (count s)))
    (is (= s #{1 2 [:f] 5}))
    (is (= #{1 2 [:f] 5} s))

    (testing "conj"
      (is (= '(5 1 2 [:f] 8)   (seq (conj s 8))))
      (is (= '(5 1 2 [:f] 10)  (seq (conj s 2 10))))
      (is (= '(5 1 2 [:f])     (seq (conj s [:f] 2 1))))
      (is (= '(5 1 2 [:f] 4 9) (seq (into s [1 2 [:f] 4 9])))))
    (testing "empty set"
      (let [s (ordered-set)]
        (is (= nil (seq s)))
        (is (= 0 (count s)))
        (is (empty? s))))
    (testing "disj"
      (is (= '(1 2 [:f])   (seq (disj s 5))))
      (is (= '(5 1 2 [:f]) (seq (disj s 42))))
      (is (= '(5)          (seq (disj s [:f] 2 1)))))))

(deftest test-transient-ordered-set
  (let [v [1 2 2 3 1 4 2 4 4 5 [:f] 7 8 8 8 9 10 10 7 7]
        s (ordered-set 1 2 3 4 5 [:f] 7 8 9 10)]
    (testing "into" ;; into defaults to using transients if the data structure supports it
      (is (= s (into (ordered-set) v))))
    (testing "conj!"
      (is (= (conj s 11)   (persistent! (conj! (transient s) 11))))
      (is (= (conj s -5)   (persistent! (conj! (transient s) -5))))
      (is (= (conj s 1)    (persistent! (conj! (transient s) 1))))
      (is (= (conj s 10)   (persistent! (conj! (transient s) 10))))
      (is (= (conj s [:f]) (persistent! (conj! (transient s) [:f]))))
      (is (= s
             (persistent!
              (loop [a v, os (transient (ordered-set))]
                (if (seq a)
                  (recur (next a) (conj! os (first a)))
                  os))))))
    (testing "disj!"
      (is (= (disj s 11)    (persistent! (disj! (transient s) 11))))
      (is (= (disj s 1)     (persistent! (disj! (transient s) 1))))
      (is (= (disj s 10)    (persistent! (disj! (transient s) 10))))
      (is (= (disj s [:f])  (persistent! (disj! (transient s) [:f]))))
      (is (= (ordered-set)
             (persistent!
              (loop [a v, os (transient s)]
                (if (seq a)
                  (recur (next a) (disj! os (first a)))
                  os))))))))

