(ns todo.main
  (:require [enfocus.core :as ef]
            [enfocus.events :as events]
            [enfocus.effects :as effects]
            [tailrecursion.javelin :refer [cell]]
            [cljs-uuid-utils :refer [make-random-uuid uuid-string]])
  (:require-macros [tailrecursion.javelin :refer [cell=]]
                   [enfocus.macros :as em]))

(defn todo-list [items]
  (ef/html [:ul 
            (for [item items]
              [:li {:class (if (:checked item) "done" "in-progress")}
                   [:input (merge {:type "checkbox" :class "check_todo" :data-rel (:id item)}
                                  (if (:checked item)
                                    {:checked "checked"}
                                    {}))]
                   [:label (:text item)]
                   [:button {:class "del_button" :data-rel (:id item)} "del"]])]))

(def todo-list-data (cell []))

(defn delete [event]
  (let [button (.-currentTarget event) 
        uuid (ef/from button (ef/get-attr :data-rel))]
        (swap! todo-list-data 
               (fn [lst] (filter #(not= (:id %) uuid) lst)))))

(defn check [event]
  (let [checkbox (.-currentTarget event)
        uuid (ef/from checkbox (ef/get-attr :data-rel))
        is_checked (ef/from checkbox (ef/get-attr :checked))]
    (.log js/console "done")))
        
        ;(swap! todo-list-data 
               ;(fn [lst] 
                 ;(conj (filter #(not= (:id %) uuid) lst))))))




(em/defaction setup-delete []
  [".del_button"] (events/listen :click delete)
  [".check_todo"] (events/listen :click check))
  


(cell= (do
         (ef/at js/document
              ["div.todo"] (ef/content (todo-list todo-list-data)))
         (setup-delete)))


(defn change []
  (let [todo-text (ef/from "#todo_text" (ef/get-prop :value))
        todo-item {:checked false
                   :id (uuid-string (make-random-uuid))
                   :text todo-text}]
    (swap! todo-list-data 
           (fn [lst] (conj lst todo-item)))
    (ef/at "#todo_text" (ef/set-prop :value ""))))

(em/defaction setup []
    ["#add_button"] (events/listen :click change))

(defn start []
  (setup))

(set! (.-onload js/window) start)
