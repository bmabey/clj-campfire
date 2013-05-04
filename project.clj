(defproject clj-campfire "2.1.0"
  :description "thin wrapper for Campfire's API"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-http "0.2.7"]
                 [clj-json "0.5.0"]]
  :profiles {:dev
             {:plugins [[lein-midje "3.0.0"]]
              :dependencies [
                             [midje "1.5.0"]
                             [com.stuartsierra/lazytest "1.2.3"]]}})
