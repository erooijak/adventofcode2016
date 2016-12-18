(ns adventofcode.day9
  (:require [clojure.string :as str]))

(def input
  (slurp "resources/day9.txt"))

(defn parse
  [d]
  (str/trim-newline d))

(def instruction-regex #"\((\d+)x(\d+)\)")

(defn instructions
  [d]
  (let [matcher (re-matcher instruction-regex d)]
    (loop [acc []]
      (if (.find matcher)
        (let [group           (.group matcher)
              matches         (re-matches instruction-regex group)
              [_ length n]    matches
              length          (Integer/parseInt length)
              n               (Integer/parseInt n)
              instruction-end (dec (.end matcher))]
          (recur (conj acc {:instruction-start (.start matcher)
                            :instruction-end   instruction-end
                            :end-index         (+ instruction-end length)
                            :n                 n
                            :length            length})))
        acc))))

(defn neutralized?
  [{:keys [instruction-end end-index]} {:keys [instruction-start]}]
  (<= instruction-end instruction-start end-index))

(defn non-neutralized
  [instructions]
  (loop [is instructions, acc []]
    (if (not-empty is)
      (let [i (first is)]
        (recur (remove (partial neutralized? i) (next is)) (conj acc i)))
      acc)))

(defn decompress
  [parsed-input instructions]
  (:decompressed
   (reduce #(let [shift      (:index-shift %1)
                  ins-length (inc (- (:instruction-end %2) (:instruction-start %2)))
                  begin      (inc (:instruction-end %2))
                  end        (inc (:end-index %2))
                  n          (dec (:n %2)) ; because the original substring remains
                  length     (:length %2)
                  addition   (apply str (repeat n (subs parsed-input begin end)))]
              (-> %1
                  (update :index-shift  (fn [shift] (+ shift (* n length) (- ins-length))))
                  (update :decompressed (fn [d] (str (subs d 0 (+ begin shift (- ins-length)))
                                                     addition
                                                     (subs d (+ begin shift)))))))
           {:index-shift 0, :decompressed parsed-input}
           instructions)))


;; Part 1

(def parsed-input (parse input))

(->> parsed-input
     instructions
     non-neutralized
     (decompress parsed-input)
     count)
