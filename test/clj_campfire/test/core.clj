(ns clj-campfire.test.core
  (:use [clj-campfire.core] :reload)
  (:use clojure.test
        midje.sweet)
  (:require [clj-http.client :as client]))

(def my-settings
  {:sub-domain "company"
   :ssl false
   :api-token "my-token"})

(deftest room-by-name-test
  (fact
   (room-by-name my-settings "Best room evar") => (contains {:id 42 :name "Best room evar" :locked false})
   (provided
    (client/request
     {:url "http://company.campfirenow.com/rooms.json"
      :method :get
      :basic-auth ["my-token" "X"], :content-type :json :accept :json})
          =>
          {:status 200, :headers {"server" "nginx/0.6.35"},
           :body "{
  \"rooms\": [
    {
      \"name\": \"Some Room\",
      \"created_at\": \"2007/02/12 21:35:28 +0000\",
      \"updated_at\": \"2010/10/28 21:16:55 +0000\",
      \"topic\": \"Some topic.\",
      \"id\": 53,
      \"membership_limit\": 50,
      \"locked\": false
    },
    {
      \"name\": \"Best room evar\",
      \"created_at\": \"2010/11/19 22:36:19 +0000\",
      \"updated_at\": \"2010/11/19 22:36:19 +0000\",
      \"topic\": \"\",
      \"id\": 42,
      \"membership_limit\": 50,
      \"locked\": false
    }]}"})))
