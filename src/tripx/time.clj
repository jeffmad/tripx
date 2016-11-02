(ns tripx.time
  (:require [clj-time.format :as tf]
            [clj-time.core :as time]))

(def trip-date-time-formats
  "a list of datetime formats that is in the order how it should be attempted
   to be parsed
    :date-hour-minute-second  2014-11-11T17:16:05
    :date 2014-11-10
    :date-time is 2014-11-11T17:16:05.816Z
    :date-hour-minute-second 2014-11-11T17:16:05
  "
  (list :date-time-no-ms
        :date-time
        :date-hour-minute-second-ms
        :date
        :date-hour-minute-second ))

(defn- silent-date-for-format
  "function that takes a string representation of a datetime and the name of a format.
        It returns a parsed datetime, or nil if it could not parse it"
  [s format]
  (try
    (tf/parse (tf/formatters format) s)
    (catch Exception e)))

(defn date-from-str
  "Lazily checks all date parsers for a pattern that matches the string, returns
   that."
  [s] (first
         (drop-while nil?
                     (map
                      (partial silent-date-for-format s) trip-date-time-formats))))

(defn time-map
  "a function that takes a date time object and returns a map whose keys are
   various forms of the time, and the values are strings representing the
   time in that format."
  [dt]
  (array-map
   :raw (tf/unparse (tf/formatters :date-time-no-ms)  dt)   ; "2014-09-12T15:00:00-07:00"
   :localized (tf/unparse (tf/formatter "MMM d, yyyy h:mm:ss aaa") dt); "Sep 12, 2014 3:00:00 PM"
   :epochSeconds (time/in-seconds (time/interval (time/epoch) dt)) ;1410559200
   :timeZoneOffsetSeconds 0; WE don't have timezone!!! -25200  (-7 * 3600)
   :localizedShortDate  (tf/unparse (tf/formatter "EEE, d MMM") dt);"Fri, 12 Sep"
   ))
