(ns clj-campfire.test.core
  (:use [clj-campfire.core] :reload)
  (:use midje.sweet)
  (:require [http.async.client :as http]))

(def my-settings
  {:sub-domain "company"
   :ssl false
   :api-token "my-token"})

(defprotocol Closeable
  (close [x]))

(def fake-client
  (reify
    Closeable
    (close [_])
    Object
    (toString [_] "fake client")))




(fact "#'room-by-name"
  (room-by-name my-settings "Best room evar") => (contains {:id 42 :name "Best room evar" :locked false})
  (provided
      (http/create-client :auth {:type :basic
                                 :user "my-token"
                                 :password "X"
                                 :preemptive false}) => fake-client
      (http/string (http/await (http/GET fake-client "http://company.campfirenow.com/rooms.json" :query {}))) =>
 "{
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
    }]}"))
