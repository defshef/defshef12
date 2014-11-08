(ns defshef12.app
  (:require [clojure.string :as string]
            [cljs.reader :refer (read-string)]
            [reagent.core :refer (atom)]))

;; define our app data so that it doesn't get over-written on reload
(defonce title (atom "Hello World"))
(defonce todos (atom (sorted-map)))
(defonce max-id (atom 0))
(add-watch todos :max-id
           (fn [_ _ _ state]
             (reset! max-id (apply max 1 (keys state)))))

(defn init-local-storage []
  (let [local-storage (.-localStorage js/window)
        storage-key "todos-defshef12"]
    ; read todos from local storage
    (if-let [data (aget local-storage storage-key)]
      (let [state (try (read-string data) (catch js/Error _ nil))]
        (apply swap! todos conj state)))
    ; write to local-storage on data change
    (add-watch todos :local-storage
               (fn [_ _ _ state]
                 (aset local-storage storage-key (pr-str state))))))

; HTML keycode values
(def ENTER 13)
(def ESCAPE 27)

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

(defn class-set
  "Given a map create an HTML element's class value
  containing every key that has a truthy value"
  [m]
  (string/join " " (for [[k v] m :when v] (name k))))

(defn todo-input [{:keys [save-input stop-input] :as props}]
  (let [save (fn [e]
               (let [elem (.-target e)]
                 (when (save-input (.-value elem))
                   (set! (.-value elem) "")
                   (stop-input))))]
    [:input
     (merge props
            {:type :text
             :auto-focus true
             :on-blur save
             :on-key-up (fn [e] (condp = (.-which e)
                                  ENTER (save e)
                                  ESCAPE (stop-input)
                                  nil))})]))

(defn todo-item []
  (let [editing (atom false)]
    (fn [{:keys [id title completed]}]
      [:li
       {:class (class-set {:completed completed
                           :editing @editing})}
       [:div.view
        [:input.toggle
         {:type :checkbox
          :checked completed
          :on-change #(toggle-todo! id)}]
        [:label
         {:on-double-click #(reset! editing true)}
         title]
        [:button.destroy
         {:on-click #(remove-todo! id)}]]

       (when @editing
         [todo-input
          {:class "edit"
           :default-value title
           :save-input #(edit-todo! id %)
           :stop-input #(reset! editing false)}])])))

(defn todo-list [todos]
  [:ul#todo-list
   (for [todo todos]
     ^{:key (:id todo)} [todo-item todo])])

(defn todo-add []
  [todo-input
   {:id "new-todo"
    :placeholder "What needs to be done?"
    :save-input add-todo!
    :stop-input (fn [])}])

(defn todo-main [todos]
  (let [all (every? :completed todos)]
    [:section#main
     [:input#toggle-all
      {:type :checkbox
       :checked all
       :on-change #(set-all-todos! (not all))}]
     [:label
      {:for :toggle-all} "Mark all as complete"]
     [todo-list todos]]))

(def filters {"All" (constantly true)
              "Active" (complement :completed)
              "Completed" :completed})

(defn todo-footer [{:keys [selected-filter pick-filter]} items]
  (let [active (count (remove :completed items))
        done (- (count items) active)]
    [:footer#footer
     [:span#todo-count
      [:strong active]
      " item" (if-not (= active 1) "s") " left"]
     [:ul#filters
      (for [label (keys filters)]
        [:li
         {:key label}
         [:a
          {:href "#"
           :class (if (= label selected-filter) "selected")
           :on-click #(pick-filter label)}
          label]])]
     (if-not (zero? done)
       [:button#clear-completed
        {:on-click clear-completed-todos!}
        "Clear Completed (" done ")"])]))

(defn todo-app []
  (let [current-filter (atom (-> filters keys first))
        pick-filter (fn [name] (if (contains? filters name)
                                 (reset! current-filter name)))]
    (fn []
      (let [selected-filter @current-filter
            all-todos (vals @todos)
            items (filter (filters selected-filter) all-todos)]
        [:section#todoapp
         [:header#header
          [:h1 @title]
          [todo-add]]
         (if-not (empty? items)
           [todo-main items])
         (if-not (empty? all-todos)
           [todo-footer
            {:selected-filter selected-filter
             :pick-filter pick-filter}
            items])]))))
