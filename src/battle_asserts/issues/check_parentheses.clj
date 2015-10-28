(ns battle-asserts.issues.check-parentheses
  (:require [clojure.test.check.generators :as gen]
            [clojure.string :as s]
            [faker.generate :as faker]))

(def level :easy)

(def description "Check if the parentheses in the expression are all balanced, so that all open parentheses are closed properly.")

(defn arguments-generator []
  (let [brackets [\( \) \space]
        right-combination ["() %s" "( %s )"]]
    (letfn [(permutation []
              (s/join (repeatedly (rand-int 10) #(rand-nth brackets))))
            (shuffled-balanced-permutation []
                                           (s/join (shuffle (seq (balanced-permutation)))))
            (balanced-permutation []
                                  (loop [n (+ (rand-int 15) 4) result ""]
                                    (if (>= (count result) n)
                                      result
                                      (recur n
                                             (format (rand-nth right-combination)
                                                     result)))))]
      (gen/tuple (gen/one-of [(gen/elements (repeatedly 50 permutation))
                              (gen/elements (repeatedly 50 shuffled-balanced-permutation))
                              (gen/elements (repeatedly 50 balanced-permutation))])))))

(def test-data
  [{:expected false
    :arguments ["( )  )"]}
   {:expected true
    :arguments ["()"]}
   {:expected true
    :arguments [" ( )(  )"]}
   {:expected true
    :arguments ["(() )"]}
   {:expected false
    :arguments [") "]}
   {:expected false
    :arguments ["("]}
   {:expected false
    :arguments [") ("]}
   {:expected false
    :arguments ["(( )"]}])

(defn solution [s]
  (let [opening (set "(")
        closing (set ")")]
    (->> s
         (reduce #(cond
                    (neg? %1) (reduced %1)
                    (opening %2) (inc %1)
                    (closing %2) (dec %1)
                    :else %1)
                 0)
         (= 0))))
