(ns cypher.cipher
  (:require [clojure.string :as str]))

(def ^:private alphabet "abcdefghijklmnopqrstuvwxyz")

(defn build-pair-map [keyword] 
  (let [keyword-chars (->> keyword str/lower-case distinct)]))
