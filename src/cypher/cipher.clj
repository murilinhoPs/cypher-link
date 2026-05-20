(ns cypher.cipher
  (:require [clojure.string :as str]))

(def ^:private alphabet "abcdefghijklmnopqrstuvwxyz")

;; passar pela keyword e construir o pair-map
;; se a letra já foi vista, pula

(defn build-pair-map [keyword]
  (let [keyword-chars (->> keyword str/lower-case distinct)]
    (reduce (fn [map letter]
              (let [index (str/index-of alphabet letter)]
               (if (or (nil? index) (contains? map letter))
                map
                nil)))
            {} ;; map inicial
            keyword-chars))) ;;current char
