(ns defshef12.main
  (:require [react]
            [reagent.core :as reagent]
            [defshef12.app]
            [defshef12.sepl]
            [figwheel.client :as figwheel]
            [clojure.browser.repl]))

(enable-console-print!)

(defn render []
  (println "Rendering page with"
           (prn-str @defshef12.app/state))
  (reagent/render-component
   [defshef12.app/app]
   (.-body js/document)))

; Stuff to run on initial page load
(defonce startup (render))

; Figwheel will run things for us after every change
(figwheel/watch-and-reload
 :on-jsload (fn [] (render)))

