(ns todo.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to todo"]
     (include-css "/css/screen.css")
     (include-js "/js/main.js")]
    [:body body]))

(defn todo-template []
  (common [:div.todo]
          [:div.todo-form
           [:input#todo_text {:type "text"}]
           [:button#add_button "Add todo"]]))


