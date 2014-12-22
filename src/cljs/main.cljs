(ns todo.main
  (:require [enfocus.core :as ef]
            [enfocus.events :as events]
            [enfocus.effects :as effects]
            [tailrecursion.javelin :refer [cell]])
  (:require-macros [tailrecursion.javelin :refer [cell=]]
                   [enfocus.macros :as em]))

(defn todo-list [items]
  (ef/html [:ul 
            (for [item items]
              [:li item])]))

(def todo-list-data (cell []))

(cell= (ef/at js/document
              ["div.todo"] (ef/content (todo-list todo-list-data))))


(em/defaction change []
    ["#add_button"] (let [todo-text (ef/from "#todo_text" (ef/get-prop :value))]
                      (swap! todo-list-data 
                             (fn [lst] (conj lst todo-text)))
                      (ef/at "#todo_text" (ef/set-prop :value ""))))

(em/defaction setup []
    ["#add_button"] (events/listen :click change))

(defn start []
  (setup))

(set! (.-onload js/window) start)
