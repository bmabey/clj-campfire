(defproject clj-campfire "2.0.0"
  :description "thin wrapper for Campfire's API"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-http "0.7.0"]
                 [cheshire "5.1.0"]]
  :profiles {:dev {:plugins [[lein-midje "3.0.0"]]
                   :dependencies [[midje "1.5.1"]]}})
