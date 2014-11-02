(ns defshef12.repl
  (:require
   [cemerick.austin :as austin]
   [cemerick.austin.repls :as repls]))

(def repl-env nil)

(defn browser-listen
  "Start the cljs repl server and wait for a browser to connect"
  []
  (def repl-env
    (reset! repls/browser-repl-env (austin/repl-env))))

(defn browser-repl
  "Start a repl using the latest repl server"
  []
  (repls/cljs-repl repl-env))

