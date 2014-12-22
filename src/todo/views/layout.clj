(ns todo.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn common [& body]
  (html5
    [:head
     [:title "Welcome to todo"]
     "<script type='text/javascript' id='lt_ws' src='http://localhost:49963/socket.io/lighttable/ws.js'></script>"
     (include-css "/css/screen.css")
     (include-js "/js/main.js")]
    [:body body]))
