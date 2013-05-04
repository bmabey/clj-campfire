(ns clj-campfire.core
  (use clj-campfire.utils)
  (require [clj-http.client :as client]
           [clj-json.core :as json]))

(defn- protocol [settings]
  (if (:ssl settings)
    "https"
    "http"))

(defn- request [settings action method options]
  (client/request
   (merge
    {:url (format "%s://%s.campfirenow.com/%s"
                  (protocol settings) (:sub-domain settings) action)
     :method method
     :accept :json
     :content-type :json
     :basic-auth [(:api-token settings) "X"]}
    options)))

(defn- post-json [settings action req]
  (request settings action :post (update-in req [:body] json/generate-string)))

(defn- get-json [settings action]
  (-> (request settings action :get {})
      :body
      json/parse-string
      keyword-keys))

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

(defn speak
  ([room msg message-type]
     (speak (meta room) (:name room) msg message-type))
  ([settings room-name msg message-type]
     (post-json settings (str "room/" (room-id settings room-name) "/speak.json")
                {:body {:message {:body msg :type message-type}}})))

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

(defn tweet
  ([room url]
     (tweet (meta room) (:name room) url))
  ([settings room-name url]
     (speak settings room-name url "TweetMessage")))
