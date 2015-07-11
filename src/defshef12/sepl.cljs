(ns defshef12.sepl
  "Save Eval Print Loop"
  (:require [defshef12.app :as app]))

(println "Edits to this text should show up in your developer console.")

; (reset! app/title "ClojureScript!")

(defn initial-data []
  (app/add-todo! "Buy Milk")
  (app/add-todo! "Warm Milk")
  (app/add-todo! "Drink Milk"))

; Uncomment this to reset data
; (do (swap! app/todos empty) (initial-data))
