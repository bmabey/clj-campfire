(defproject clj-campfire "2.0.0-SNAPSHOT"
  :description "thin wrapper for Campfire's API"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [clj-http "0.2.7"]
                 [clj-json "0.5.0"]]
  :dev-dependencies [[midje "1.3.1" :exclusions [org.clojure/clojure]]
                     [lein-midje "1.0.7"]
                     [swank-clojure "1.3.4"]])
