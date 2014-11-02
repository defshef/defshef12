(ns defshef12.app
   (:require [reagent.core :refer (atom)]))

;; define your app data so that it doesn't get over-written on reload
(defonce state (atom {
  :title "World"
  :todos [{:text "Buy Milk" :state :todo}
          {:text "Warm Milk" :state :todo}
          {:text "Drink Milk" :state :todo}]}))

(defn toggle [i]
  (swap! state update-in [:todos i :state]
         (fn [state] (if (= state :todo) :done :todo))))

(defn todo-item [i todo]
  (let [checked (= :done (:state todo))]
    [:li
      (merge
        {:on-click #(toggle i)}
       (when checked
         {:class :completed}))
     [:div.view
      [:input.toggle
       {:type :checkbox :checked checked :readOnly true}]
      [:label
       (:text todo)]]]))

(defn todo-list [todos]
  [:ul#todo-list
   (map-indexed
     (fn [i todo] ^{:key i} [todo-item i todo])
     todos)])

(defn app []
  (let [data @state]
  [:section#todoapp
   [:header#header
    [:h1 "Hello " (:title data)]]
   [:section#main
    [todo-list (:todos data)]]]))