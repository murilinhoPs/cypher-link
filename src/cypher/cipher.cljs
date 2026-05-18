(ns cypher.cipher
  (:require [clojure.string :as str]))

(def ^:private alphabet "abcdefghijklmnopqrstuvwxyz")

(defn build-pair-map [kw]
  (let [kw-chars (->> kw str/lower-case distinct)]
    (reduce
      (fn [m ch]
        (let [idx (str/index-of alphabet (str ch))]
          (if (or (nil? idx) (contains? m ch))
            m
            (let [partner (get alphabet (if (= idx 25) 24 (inc idx)))]
              (if (contains? m partner)
                m
                (assoc m ch partner partner ch))))))
      {}
      kw-chars)))

(defn cipher [text kw]
  (let [pair-map (build-pair-map kw)]
    (->> text
         str/lower-case
         (map #(get pair-map % %))
         (apply str))))
