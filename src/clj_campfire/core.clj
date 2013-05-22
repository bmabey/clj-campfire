(ns clj-campfire.core
  (:use clj-campfire.utils
        [clojure.walk :only [keywordize-keys]])
  (:require [http.async.client :as http]
            [cheshire.core :as json])
  (:import (java.io File IOException)))

(defn- protocol [settings]
  (if (:ssl settings)
    "https"
    "http"))

(defn- get-client [settings & {:keys [preemptive] :or {preemptive false}}]
  (http/create-client :auth {:type :basic
                             :user (:api-token settings)
                             :password "X"
                             :preemptive preemptive}))

(defn- build-url [settings action]
  (format "%s://%s.campfirenow.com/%s"
          (protocol settings) (:sub-domain settings) action))

(defn- post-json [settings action & {:keys [body preemptive] 
                                     :or {body (json/generate-string {}) 
                                          preemptive false}}]
  (with-open [client (get-client settings :preemptive preemptive)]
    (let [response (http/POST client (build-url settings action)
                              :headers {:Content-Type "application/json"}
                              :body body)]
      (-> response
          http/await
          http/string
          json/parse-string
          keywordize-keys))))

(defn- get-json
  [settings action & {:keys [query] :or {query {}}}]
  (with-open [client (get-client settings)]
    (let [response (http/GET client (build-url settings action) :query query)]
      (-> response
          http/await
          http/string
          json/parse-string
          keywordize-keys))))

(defn my-info [settings]
  (get-json settings "users/me.json"))

(defn get-user [settings user-id]
  (get-json settings (str "users/" user-id ".json")))

(defn account [settings]
  (get-json settings "account.json"))

(defn rooms [settings]
  (for [room (:rooms (get-json settings "rooms.json"))]
    (with-meta room settings)))

(defn room-by-name [settings room-name]
  (let [room-name (name room-name)]
    (->> (rooms settings)
         (filter #(= (:name %) room-name))
         first)))

(def room-id
  (memoize
   (fn [settings room-name]
     (:id (room-by-name settings room-name)))))

(defn join-room
  [settings room-name]
  (post-json settings (str "room/" (room-id settings room-name) "/join.json")))

(defn leave-room
  [settings room-name]
  (post-json settings (str "room/" (room-id settings room-name) "/leave.json")))

(defn room-info
  [settings room-name]
  (get-json settings (str "room/" (room-id settings room-name) ".json")))

(defn list-room
  [settings room-name]
  (map :name
       (-> (room-info settings room-name)
           :room
           :users)))

(defn upload
  ([room file] (upload (meta room) (:name room) file))
  ([settings room-name file]
     (post-json settings 
                (str "room/" (room-id settings room-name) "/uploads.json")
                :body [{:type      :file
                        :name      "upload"
                        :file      (File. file)
                        }]  ; :mime-type "application/octet-stream"}]
                :preemptive true)))

(defn speak
  ([room msg message-type]
     (speak (meta room) (:name room) msg message-type))
  ([settings room-name msg message-type]
     (post-json settings (str "room/" (room-id settings room-name) "/speak.json")
                :body (json/generate-string 
                       {:message {:body msg :type message-type}}))))

(defn message
  ([room msg]
     (message (meta room) (:name room) msg))
  ([settings room-name msg]
      (speak settings room-name msg "TextMessage")))

(defn paste
  ([room msg]
     (paste (meta room) (:name room) msg))
  ([settings room-name msg]
      (speak settings room-name msg "PasteMessage")))

(defn play-sound
  ([room sound]
     (play-sound (meta room) (:name room) sound))
  ([settings room-name sound]
     (speak settings room-name sound "SoundMessage")))

(defn messages
  ([room]
     (messages (meta room) (:name room)))
  ([settings room-name & {:keys [limit since-message]
                          :or {limit 100 since-message 0}}]
     (let [options {:limit limit :since_message_id since-message}]
       (get-json settings
                 (str "room/" (room-id settings room-name) "/recent.json")
                 :query options))))

(defn stream-messages
  "Calls handler passing a lazy seq of messages as the single argument"
  [settings room-name handler]
  (let [streaming-url (build-url (assoc settings :sub-domain "streaming")
                                 (str "room/"
                                      (room-id settings room-name)
                                      "/live.json"))]
    (with-open [client (get-client settings :preemptive true)]
      (let [response (http/stream-seq client :get streaming-url :timeout -1)]
        (handler
         (filter identity
                 (map (fn [chunk] (-> chunk json/parse-string keywordize-keys))
                      (http/string response))))
        (http/cancel response)))))
