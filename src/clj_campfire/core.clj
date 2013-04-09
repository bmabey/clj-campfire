(ns clj-campfire.core
  (use clj-campfire.utils)
  (require [http.async.client :as http]
           [cheshire.core :as json]))

(defn- protocol [settings]
  (if (:ssl settings)
    "https"
    "http"))

(defn- get-client [settings]
  (http/create-client :auth {:type :basic 
                             :user (:api-token settings) 
                             :password "X"}))

(defn- build-url [settings action] 
  (format "%s://%s.campfirenow.com/%s"
          (protocol settings) (:sub-domain settings) action))
  
(defn- post-json [settings action req]
  (with-open [client (get-client settings)]
    (let [response (http/POST client (build-url settings action) 
                              :headers {:Content-Type "application/json"}
                              :body (json/generate-string (:body req)))]
      (-> response
          http/await
          http/string
          json/parse-string
          keyword-keys))))

(defn- get-json 
  [settings action & {:keys [query] :or {query {}}}]
  (with-open [client (get-client settings)]
    (let [response (http/GET client (build-url settings action) :query query)]
      (-> response
          http/await
          http/string
          json/parse-string
          keyword-keys))))

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

(defn messages
  ([room] 
     (messages (meta room) (:name room)))
  ([settings room-name & {:keys [limit since-message]
                          :or {limit 100 since-message 0}}]
     (let [options {:limit limit :since_message_id since-message}]
       (get-json settings 
                 (str "room/" (room-id settings room-name) "/recent.json")
                 :query options))))
