(ns todo.main
  (:require [enfocus.core :as ef]
            [enfocus.events :as events]
            [enfocus.effects :as effects]
            [tailrecursion.javelin :refer [cell]])
  (:require-macros [tailrecursion.javelin :refer [cell=]]
                   [enfocus.macros :as em]))

(def a (cell "None"))

(defn header [text]
  (ef/html [:div
            [:h1 text]]))

(cell= (ef/at js/document
              ["body"] (ef/content (header a))))

(defn start []
  (swap! a (fn [old] "Hello enfocus !!!")))

(set! (.-onload js/window) start)
