(ns todo.main
  (:require [enfocus.core :as ef]
            [enfocus.events :as events])
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
  ;;      for generetiong (not= (:id %) uuid) predicate
  (let [button (.-currentTarget event)
        uuid (ef/from button (ef/get-attr :data-rel))]
    (swap! todo-state (partial filterv #(not= (str (:id %)) uuid)))))

(defn check-todo [event]
  ;; TODO Add higher-order functions
  ;;      for generetiong (= (:id %) uuid) predicate
  ;;      use complement
  (let [checkbox (.-currentTarget event)
        uuid (ef/from checkbox (ef/get-attr :data-rel))
        checked (ef/from checkbox (ef/get-attr :checked))
        is_checked (not (= "checked" checked))]
    (swap! todo-state (partial mapv #(if (= (str (:id %)) uuid)
                                      (assoc % :checked is_checked)
                                      %)))))

(defn render-todo [todo-list]
    (ef/at ["div.todo"] (ef/content (todo-block todo-list)))
    (ef/at [".del_button"] (events/listen :click delete-todo))
    (ef/at [".check_todo"] (events/listen :click check-todo)))

(add-watch todo-state nil
  (fn[_ _ _ todo-list] (render-todo todo-list)))

(defn add-todo []
  (let [todo-text (ef/from "#todo_text" (ef/get-prop :value))
        todo-item {:checked false
                   :id (rand-int 1000)
                   :text todo-text}]
    (swap! todo-state #(conj % todo-item))
    (ef/at "#todo_text" (ef/set-prop :value ""))))

(defn start []
  (reset! todo-state [{:checked false
                       :id (rand-int 1000)
                       :text "Start workshop"}
                      {:checked false
                       :id (rand-int 1000)
                       :text "Show how clojure work"}
                      {:checked false
                       :id (rand-int 1000)
                       :text "Show repl power"}])
  (ef/at ["#add_button"] (events/listen :click add-todo)))

(set! (.-onload js/window) start)

;; TODO Use functions from workshop like active-todo ans others

;; TODO Add handle for hit 'Enter' at input#todo_text. It should add new todo.
;;      Event name is :keypress
;;      Event has attr .-keyCode
;;      Enter key-code is 13

;; TODO High level task
;;      Render count of active and done todo under todo-list

(defn bootstrap-todo [id checked text]
  {:checked checked
   :id id
   :text text})

(defn new-todo [checked text]
  (let [id (rand-int 1000)]
    (bootstrap-todo id checked text)))

(def active-todo (partial new-todo false))
(def done-todo (partial new-todo true))

(def done? :checked)
(def active? (complement done?))

(defn get-todo-generator [create-todo]
  (fn [& texts]
    (map create-todo texts)))

(def active-todo-generator (get-todo-generator active-todo))
(def done-todo-generator (get-todo-generator done-todo))

(def random-todo-generator (get-todo-generator #(new-todo (= 0 (rand-int 2)) %)))

(def text "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.")

(def random-todo-texts (clojure.string/split text #"\s"))

(comment
  (reset! todo-state  (apply random-todo-generator random-todo-texts))
)
