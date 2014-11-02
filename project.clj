(defproject defshef12 "0.1.0-SNAPSHOT"
  :description "Reagent Workshop"
  :url "http://defshef.github.io"
  :license {:name "MIT"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]

                 [reagent "0.4.3"]

                 [compojure "1.2.1"]
                 [enlive "1.1.5"]

                 [figwheel "0.1.5-SNAPSHOT"]
                 [org.clojure/tools.nrepl "0.2.5"]
                 [com.cemerick/austin "0.1.5"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-figwheel "0.1.5-SNAPSHOT"]
            [com.cemerick/austin "0.1.5"]]

  :source-paths ["src"]

  :repl-options {:port 1717
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/main.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map true
                         :optimizations :none
                         :foreign-libs [{:file "reagent/react.js"
                                         :provides ["react"]}]}}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/main.min.js"
                         :optimizations :advanced
                         :pretty-print false
                         :closure-warnings {:non-standard-jsdoc :off}
                         :foreign-libs [{:file "reagent/react.js"
                                         :provides ["react"]}]}}]}

    :figwheel {
      :http-server-root "public"
      :server-port 3449
      :css-dirs ["public/resources/css"]
      :ring-handler defshef12.server/handler
    })
