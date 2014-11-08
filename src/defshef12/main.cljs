(ns defshef12.main
  (:require [react]
            [reagent.core :as reagent]
            [defshef12.app :as app]
            [defshef12.sepl :as sepl]
            [figwheel.client :as figwheel]
            [clojure.browser.repl]))

;; printing goes to browser console
(let [c js/console
      log (.-log c)]
  (set-print-fn!
   (fn [& args]
     (let [args (if (= (last args) "\n") (butlast args) args)]
       (.apply log c (into-array args))))))

(defn render []
  (reagent/render-component
   [app/todo-app]
   (.getElementById js/document "main-area")))

; Stuff to run on initial page load
(defonce startup (do
                   (app/init-local-storage)
                   (when (empty? @app/todos)
                     (sepl/initial-data))
                   (render)))

; Figwheel will run things for us after every change
(figwheel/watch-and-reload
 :on-jsload (fn [] (render)))

