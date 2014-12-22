(ns todo.routes.home
  (:require [compojure.core :refer :all]
            [todo.views.layout :as layout]))

(defn home []
  (layout/todo-template))

(defroutes home-routes
  (GET "/" [] (home)))
