# clj-campfire

Very thin wrapper around 37 Signal's [Campfire](http://campfirenow.com/) API for basic communication.



## Usage

    (def cf-settings
      {:api-token "my-token",
       :ssl true,
       :sub-domain "mycompany"})

    (require '[clj-campfire.core :as cf])

    (cf/message cf-settings "Room 1" "Hello")
    (cf/paste cf-settings "Room 1" "lots of information here....")
    (cf/play-sound cf-settings "Room 1" "rimshot")
    (cf/messages cf-settings "Room 1" :limit 10)
    (cf/stream-messages cf-settings "Room 1" (fn [m] (println m))) ; runs forever

You can also use a room as your arguments to the speak functions:

    (def my-room (cf/room-by-name "Room 2"))
    (cf/message my-room "Hello")


## Installation

`clj-campfire` is available as a Maven artifact [Clojars](http://clojars.org/clj-campfire).

Leiningen:

    :dependencies
      [[clj-campfire "2.1.0"] ...]

Maven:

    <dependency>
      <groupId>clj-campfire</groupId>
      <artifactId>clj-campfire</artifactId>
      <version>2.1.0</version>
    </dependency>

## License

Copyright (C) 2011 Ben Mabey and LeadTune

Released under the MIT License: <http://www.opensource.org/licenses/mit-license.php>
