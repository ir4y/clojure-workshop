(ns todo.main
  (:require [enfocus.core :as ef]
            [enfocus.events :as events]
            [enfocus.effects :as effects]
            [cljs-uuid-utils :refer [make-random-uuid uuid-string]])
  (:require-macros [enfocus.macros :as em]))


(def todo-state (atom []))

(defn todo-block [items]
  ;; TODO move (:id item) to :let param
  ;;      https://clojuredocs.org/clojure.core/for
  (ef/html [:ul 
            (for [item items]
              [:li {:class (if (:checked item) "done" "in-progress")}
                   [:input (merge {:type "checkbox" 
                                   :class "check_todo"
                                   :id (str "checkbox_" (:id item)) 
                                   :data-rel (:id item)}
                                  (if (:checked item)
                                    {:checked "checked"}
                                    {}))]
                   [:label {:for (str "checkbox_" (:id item))} (:text item)]
                   [:button {:class "del_button" :data-rel (:id item)} "del"]])]))

(defn delete-todo [event]
  ;; TODO Add higher-order functions
  ;;      for generetiong (= (:id %) uuid) predicate
  (let [button (.-currentTarget event) 
        uuid (ef/from button (ef/get-attr :data-rel))]
        (swap! todo-state (partial remove #(= (:id %) uuid)))))

(defn check-todo [event]
  ;; TODO Use previosly higher-order function
  (let [checkbox (.-currentTarget event)
        uuid (ef/from checkbox (ef/get-attr :data-rel))
        checked (ef/from checkbox (ef/get-attr :checked))
        is_checked (not (= "checked" checked))]
    (swap! todo-state (partial map #(if (= (:id %) uuid)
                                      (assoc % :checked is_checked)
                                      %)))))

(em/defaction setup-todo-actions []
  [".del_button"] (events/listen :click delete-todo)
  [".check_todo"] (events/listen :click check-todo))

(defn render-todo [todo-list]
  (do
    (ef/at js/document
           ["div.todo"] (ef/content (todo-block todo-list)))
    (setup-todo-actions)))

(add-watch todo-state nil 
  (fn[_ _ _ todo-list] (render-todo todo-list)))

(defn get-uuid []
  (uuid-string (make-random-uuid)))

(defn add-todo []
  (let [todo-text (ef/from "#todo_text" (ef/get-prop :value))
        todo-item {:checked false
                   :id (get-uuid)
                   :text todo-text}]
    (swap! todo-state #(conj % todo-item))
    (ef/at "#todo_text" (ef/set-prop :value ""))))

(em/defaction setup-add-action []
    ["#add_button"] (events/listen :click add-todo))

(defn start []
  (reset! todo-state [{:checked false
                       :id (get-uuid)
                       :text "Start workshop"}
                      {:checked false
                       :id (get-uuid)
                       :text "Show how clojure work"}
                      {:checked false
                       :id (get-uuid)
                       :text "Show repl power"}])
    (setup-add-action))

(set! (.-onload js/window) start)

;; TODO Use functions from workshop like active-todo ans others

;; TODO Add handle for hit 'Enter' at input#todo_text. It should add new todo.
;;      Event name is :keypress
;;      Event has attr .-keyCode
;;      Enter key-code is 13


;; TODO High level task
;;      Render count of active and done todo under todo-list
