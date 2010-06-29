(ns test-ordered-set
  (:use ordered-set)
  (:use clojure.test))

(deftest test-ordered-set
  (testing "ordered-set"
    (let [s (ordered-set 5 1 2 3)]
      (is (= '(5 1 2 3) (seq s)))
      (is (= 4 (count s)))

      (testing "conj"
        (let [s (conj s 8)]
          (is (= '(5 1 2 3 8) (seq s)))
          (is (= 5 (count s))))
        (let [s (conj s 2 10)]
          (is (= '(5 1 2 3 10) (seq s)))
          (is (= 5 (count s))))
        (let [s (conj s 3 2 1)]
          (is (= '(5 1 2 3) (seq s)))
          (is (= 4 (count s))))
        (let [s (into s [1 2 3 4 9])]
          (is (= '(5 1 2 3 4 9) (seq s)))
          (is (= 6 (count s)))))
      (testing "empty set"
        (let [s (ordered-set)]
          (is (= nil (seq s)))
          (is (= 0 (count s)))
          (is (empty? s))))
      (testing "disj"
        (let [s (disj s 5)]
          (is (= '(1 2 3) (seq s)))
          (is (= 3 (count s))))
        (let [s (disj s 3 2 1)]
          (is (= '(5) (seq s)))
          (is (= 1 (count s))))))))

