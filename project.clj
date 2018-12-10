(defproject clj-campfire "2.2.0"
  :description "thin wrapper for Campfire's API"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [http.async.client "1.2.0"]
                 [cheshire "5.1.0"]]
  :profiles {:dev 
             {:plugins [[lein-midje "3.2.1"]]
              :dependencies [[org.clojure/clojure "1.9.0"]
                             [midje "1.6.0"]
                             [org.clojure/core.unify "0.5.7"]]}})
