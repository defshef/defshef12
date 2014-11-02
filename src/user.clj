(ns user
  (:require
   [cemerick.austin :as austin]
   [cemerick.austin.repls :as repls]))

(def repl-env (atom nil))

(defn browser-listen
  "Start the cljs repl server and wait for a browser to connect"
  []
  (reset! repl-env
    (reset! repls/browser-repl-env (austin/repl-env))))

(defn browser-repl
  "Start a repl using the latest repl server"
  []
  (when (nil? @repl-env)
    (browser-listen))
  (repls/cljs-repl @repl-env))
