(ns adventofcode.2016.day07
  (:require
   [clojure.java.io :as io]))

(def input
  (-> "2016/day07.txt" io/resource io/reader line-seq))

(defn parse
  [d]
  (map (juxt #(map second (re-seq #"\](.*?)\[" (str "]" % "[")))
             #(map second (re-seq #"\[(.*?)\]" %)))
       d))

(defn parts
  [size s]
  (->> (range size)
       (mapcat #(->> (partition size (subs s %))
                     (map (fn [cs] (apply str cs)))))
       concat))

(defn abba?
  [s]
  (->> s
       (parts 4)
       (some #(re-find #"(?=(.)(.)\2\1)(.)(?!\1).*" %))))

(defn tls?
  [[inside outside]]
  (and (some abba? inside)
       (not (some abba? outside))))

(->> input
     parse
     (filter tls?)
     count)
;; => 115

(defn candidates
  [s]
  (->> s
       (parts 3)
       (filter #(re-find #"(?=(.).\1)(.)(?!\1).*" %))))

(defn ssl?
  [[inside outside]]
  (let [abas (mapcat candidates inside)
        babs (mapcat candidates outside)]
    (some #(some (fn [[a b]] (let [bab (str b a b)]
                               (= bab %)))
                 abas)
          babs)))

(->> input
     parse
     (filter ssl?)
     count)
;; => 231
