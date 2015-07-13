(ns defshef12.app
  (:require [clojure.string :as string]
            [cljs.reader :refer (read-string)]
            [reagent.core :refer (atom)]))


; *************************
; ** define our app data **
; *************************

; we use (defonce) so it doesn't get replaced on reload
(defonce title (atom "TodoMVC"))
(defonce todos (atom (sorted-map)))
(defonce max-id (atom 0))
(add-watch todos :max-id
           (fn [_ _ _ state]
             (reset! max-id (apply max 1 (keys state)))))



; **************************************
; ** functions to manipulate app data **
; **************************************

; functions ending in ! make modifications

(defn add-todo! [text]
  (let [title (string/trim text)]
    (if-not (empty? title)
      (let [id (swap! max-id inc)
            item {:id id :title text :completed false}]
        (swap! todos assoc id item)))))

(defn edit-todo! [id text]
  (let [title (string/trim text)]
    (if-not (empty? title)
      (swap! todos assoc-in [id :title] title))))

(defn toggle-todo! [id]
  (swap! todos update-in [id :completed] not))

(defn remove-todo! [id]
  (swap! todos dissoc id))

(defn fmap
  "Given a map `m`, apply `f` to each value, maintaining keys"
  [f m]
  (reduce-kv (fn [acc k v] (assoc acc k (f v))) m m))

(defn set-all-todos! [state]
  (swap! todos (partial fmap #(assoc % :completed state))))

(defn clear-completed-todos! []
  (swap! todos #(into {} (remove (comp :completed val) %))))




;; Some UI helper functions

; HTML keycode values
(def ENTER 13)
(def ESCAPE 27)

(defn class-set
  "Given a map create an HTML element's class value
  containing every key that has a truthy value"
  [m]
  (string/join " " (for [[k v] m :when v] (name k))))




; *******************************
; ** user interface components **
; *******************************
(declare todo-app todo-add todo-main todo-item todo-input todo-footer)

(def filters
  {"All"       (fn [] true)
   "Completed" (fn [todo] (:completed todo))})

(defn todo-app
  "The whole application"
  []
  (let [current-filter (atom "All")
        pick-filter (fn [name] (if (contains? filters name)
                                 (reset! current-filter name)))]
    (fn []
      (let [selected-filter @current-filter
            all-todos (vals @todos)
            items (filter (filters selected-filter) all-todos)]
        [:section
         [:header
          [:h1 @title]
          [todo-add]]
         (if-not (empty? items)
           [todo-main items])
         (if-not (empty? all-todos)
           [todo-footer
            {:selected-filter selected-filter
             :pick-filter pick-filter}
            items])]))))

(defn todo-add
  "The input for adding todos"
  []
  [todo-input
   {:id "add-todo"
    :placeholder "What needs to be done?"
    :save-input add-todo!
    :stop-input (fn [])}])

(defn todo-main
  "The middle section of the app"
  [todos]
  (let [all (every? :completed todos)]
    [:section
     [:input#toggle-all
      {:type :checkbox
       :checked all
       :on-change #(set-all-todos! (not all))}]
     [:label
      {:for :toggle-all} "Mark all as complete"]
     [:ul
      (for [todo todos]
        ^{:key (:id todo)} [todo-item todo])]]))

(defn todo-item
  "A todo in the list"
  []
  (let [editing (atom false)]
    (fn [{:keys [id title completed]}]
      [:li
       {:class (class-set {:completed completed
                           :editing @editing})}
       [:div
        [:input
         {:type :checkbox
          :checked completed
          :on-change #(toggle-todo! id)}]
        [:label
         {:on-double-click #(do (reset! editing true) nil)}
         title]
        [:button
         {:on-click #(remove-todo! id)}]]

       (when @editing
         [todo-input
          {:default-value title
           :save-input #(edit-todo! id %)
           :stop-input #(reset! editing false)}])])))

(defn todo-input
  "The actual todo input box"
  [{:keys [save-input stop-input] :as props}]
  (let [save (fn [e]
               (let [elem (.-target e)]
                 (when (save-input (.-value elem))
                   (set! (.-value elem) "")
                   (stop-input)
                   nil)))]
    [:input
     (merge props
            {:type :text
             :auto-focus true
             :on-blur save
             :on-key-up (fn [e] (condp = (.-which e)
                                  ENTER (save e)
                                  ESCAPE (stop-input)
                                  nil))})]))

(defn todo-footer
  "The footer component"
  [{:keys [selected-filter pick-filter]} items]
  (let [active (count (remove :completed items))
        done (- (count items) active)]
    [:footer
     [:span
      [:strong active]
      " item" (if-not (= active 1) "s") " left"]
     [:ul
      [:li [:a {:href "#"
                :class (if (= "All" selected-filter) "selected")
                :on-click #(pick-filter "All")}
            "All"]]
      [:li [:a {:href "#"
                :class (if (= "Completed" selected-filter) "selected")
                :on-click #(pick-filter "Completed")}
            "Completed"]]]
     (if-not (zero? done)
       [:button
        {:on-click clear-completed-todos!}
        "Clear Completed (" done ")"])]))




(defn init-local-storage
  "Setup syncing between todos & window.localStorage"
  []
  (let [local-storage (aget js/window "localStorage")
        storage-key "todos-defshef12"]
    ; read todos from local storage
    (if-let [data (aget local-storage storage-key)]
      (let [state (try (read-string data) (catch js/Error _ nil))]
        (apply swap! todos conj state)))
    ; write to local-storage on data change
    (add-watch todos :local-storage
               (fn [_ _ _ state]
                 (aset local-storage storage-key (pr-str state))))))