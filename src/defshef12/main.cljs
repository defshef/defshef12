(ns ^:figwheel-always defshef12.main
  (:require [reagent.core :as reagent]
            [defshef12.app :as app]
            [defshef12.sepl :as sepl]))

; printing goes to browser console
(enable-console-print!)

(defn render []
  (reagent/render-component
   [app/todo-app]
   (.getElementById js/document "app")))

; Stuff to run on initial page load
(defonce startup (do
                   (app/init-local-storage)
                   (when (empty? @app/todos)
                     (sepl/initial-data))
                   (render)))

; This function will be run on every change
(defn on-js-reload []
  (render))
