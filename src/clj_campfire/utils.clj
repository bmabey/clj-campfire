(ns clj-campfire.utils)


(defn map-keys-and-vals
  "Transforms the keys and vals in m using fk and fv"
  [fk fv m]
  (persistent!
   (reduce (fn [m [k v]] (assoc! m (fk k) (fv v)))
           (transient {}) m)))

(defprotocol KeywordKeys
  (keyword-keys [e] "Converts keys in maps to keywords"))

(extend-protocol KeywordKeys
  Object
  (keyword-keys [x] x)
  clojure.lang.PersistentVector
  (keyword-keys [v]
                (map keyword-keys v))
  clojure.lang.PersistentArrayMap
  (keyword-keys [m]
                (map-keys-and-vals keyword keyword-keys m)))
