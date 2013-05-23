(defproject clj-campfire "2.2.0"
  :description "thin wrapper for Campfire's API"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [http.async.client "0.5.2"]
                 [cheshire "5.1.0"]]
  :profiles {:dev 
             {:plugins [[lein-midje "3.0.0"]]
              :dependencies [[midje "1.5.1"]]}})
