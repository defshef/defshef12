(defproject defshef12 "0.1.0-SNAPSHOT"
  :description "ClojureScript Workshop"
  :url "http://defshef.github.io"
  :license {:name "MIT"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "0.0-3308"]
                 [reagent "0.5.0" :exclusions [cljsjs/react]]
                 [cljsjs/react "0.13.3-0"]]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.7"
             :exclusions [org.clojure/clojure
                          org.codehaus.plexus/plexus-utils]]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]

              :figwheel { :on-jsload "defshef12.main/on-js-reload" }

              :compiler {:main defshef12.main
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/defshef12.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true }}]}

  :figwheel {:css-dirs ["resources/public/css"] ;; watch and update CSS
             })
