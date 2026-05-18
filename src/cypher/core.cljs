(ns cypher.core
  (:require [uix.core :refer [defui $]]
            [uix.dom]
            [clojure.string :as str]
            [cypher.ui.encrypt :as encrypt]
            [cypher.ui.decrypt :as decrypt]))

(defn current-view []
  (let [hash js/window.location.hash]
    (cond
      (str/starts-with? hash "#msg=") :decrypt
      :else                           :encrypt)))

(defui app []
  (case (current-view)
    :decrypt ($ decrypt/view)
    :encrypt ($ encrypt/view)))

(defn init! []
  (uix.dom/render ($ app) (js/document.getElementById "root")))
