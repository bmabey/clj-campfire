(ns clj-campfire.test.utils
  (:use [clj-campfire.utils] :reload)
  (:use midje.sweet))

(facts #'keyword-keys
  (keyword-keys {"foo" 42 "bar" 73}) => {:foo 42 :bar 73}
  (keyword-keys {"nested" {"map" 42 "bar" 23}}) => {:nested {:map 42 :bar 23}}
  (keyword-keys {"vecs" [{"foo" 42} {"nested" [23 {"stuff" {"is" "here"}}]}]})
  => {:vecs [{:foo 42} {:nested [23 {:stuff {:is "here"}}]}]})
