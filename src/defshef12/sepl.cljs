(ns defshef12.sepl
  "Save Eval Print Loop"
  (:require [defshef12.app :as app]))

; (reset! app/title "ClojureScript!")

(defn initial-data []
  (app/add-todo! "Buy Milk")
  (app/add-todo! "Warm Milk")
  (app/add-todo! "Drink Milk"))

(defonce setup (initial-data))

; Uncomment this to reset data
;; (do (reset! app/todos (sorted-map)) (initial-data))
