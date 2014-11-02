(ns defshef12.server
  (:require [net.cgrand.enlive-html :as enlive]
            [clojure.tools.nrepl.server :as nrepl]
            [cemerick.piggieback :as piggieback]
            [cemerick.austin.repls :as austin]
            [compojure.core :refer (GET)]
            [clojure.java.io :as io]))

(defonce nrepl
  (let [port 1717]
    (require 'user) ; load repl helper fns
    (nrepl/start-server
     :port port
     :handler (nrepl/default-handler #'piggieback/wrap-cljs-repl))
    (println "nREPL server listening on localhost port" port)))

(enlive/deftemplate page
  (io/resource "public/index.html")
  []
  [:body] (enlive/append
           (enlive/html [:script (austin/browser-connected-repl-js)])))

(def handler
  (GET "/*" _ (page)))
