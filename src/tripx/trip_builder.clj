(ns tripx.trip-builder
  (:require [tripx.trip-parser :as t]
            [tripx.activity :as a]
            [tripx.time :as time]
            [tripx.step :refer [take-step]]
            [tripx.activity-parser :as ap]))

(defn stop-if-error [v]
  (if (= :ok (get v :status))
    v
    (reduced v)))

(defn upload-plan [req]
  [{:action :upload
    :file (get-in req [:params :doc])
    :max-size 2000
    :name :doc}
   {:action :notify
    :message (fn [state] (get-in state [:data :doc]))
    :target [:somebody]}])

#_(tb/execute (tb/build-trip-steps {:host "wwwexpediacom.integration.sb.karmalab.net"
                                    :siteid 1
                                    :locale "en_US"
                                    :tripid "123"}))
(comment (pprint (:trip (tb/execute-xf (tb/build-trip-steps {:host "wwwexpediacom.integration.sb.karmalab.net"
                                                             :siteid 1
                                                             :locale "en_US"
                                                             :tripid "123"})))))

#_(pprint (map :trip (reductions t/take-step {:status :ok} (t/build-trip-steps {:host "h" :siteid 1 :locale "en_US" :tripid "123"}))))

; is it better to have each step get everything it needs and be able to run
; independently (pass in some params here in the plan)
; or is it ok to have dependencies in the state from previous steps?
(defn build-trip-steps [req]
  [{:step :read-trip
    :site-id (get-in req [:siteid])
    :locale (get-in req [:locale])
    :trip-id (get-in req [:tripid])
    :itinerary-number (get-in req [:itinerary-number])
    :trip-number (get-in req [:trip-number])
    :host (get-in req [:host])}
   {:step :parse-trip}
   {:step :verify-trip-status}
   {:step :add-skeleton}
   {:step :trip-id}
   {:step :web-details-url}
   {:step :trip-number}
   {:step :title}
   ;{:step :fail}
   {:step :update-trip-name-url}
   ;{:step :trip-start-time}
   ;{:step :trip-end-time}
   {:step :trip-created-time}
   {:step :trip-updated-time}
   {:step :trip-cache-updated-time}
   {:step :booking-status}
   {:step :rewards}
   ;{:step :sharable-url}
   ;{:step :customer-support}
   ;{:step :booked-with-foreign-currency}
   {:step :activities}
   {:step :read-trip-error}
   {:step :trip-source}
   {:step :is-mrp}
   ])

(defmethod take-step :read-trip
  [state step]
  (let [s (slurp (.toString (clojure.java.io/resource "trips/xml/activity1.xml")))
        siteid (:siteid step) ; or use {:keys [siteid locale trip-id]} step
        locale (:locale step)
        trip-id (:trip-id step)]
    (-> state
        (assoc :host (:host step))
        (assoc :trip-xml s)))) ; if http status not 200 add errors to state and change status

(defmethod take-step :parse-trip
  [state step]
  (->> (:trip-xml state)
       t/parse-xml
       (assoc state :z)))

(defmethod take-step :verify-trip-status
  [state step]
  "verify that status code inside trip xml is 200"
  (if (not= "200" (t/status-code (:z state))) ; also log it
    (assoc state :status :trip-read-error)
    state))

(defmethod take-step :add-skeleton
  [state step]
  (assoc state :trip (array-map :apiVersion "1.0"
                                :responseType "TRIP_DETAILS"
                                :responseData (array-map :levelOfDetail "FULL"))))

(defmethod take-step :trip-id
  [state step]
  (assoc-in state [:trip :responseData :tripId]
            (t/trip-id (:z state))))

(defmethod take-step :web-details-url
  [state step]
  (let [h (:host state)
        url (str "https://" h "/trips/" (t/trip-number (:z state)))]
    (assoc-in state [:trip :responseData :webDetailsURL] url)))

(defmethod take-step :trip-number
  [state step]
  (assoc-in state [:trip :responseData :tripNumber] (t/trip-number (:z state))))

(defmethod take-step :title
  [state step]
  (assoc-in state [:trip :responseData :title] (t/title (:z state))))

(defmethod take-step :fail
  [state step]
  (assoc state :status :fatal-error))

(defmethod take-step :update-trip-name-url
  [state step]
  (let [h (:host state)
        trip-id (t/trip-id (:z state))
        url (str "https://" h "/api/trips/" trip-id "/updateTripNameDescription")]
    (assoc-in state [:trip :responseData :updateTripNameDescPathURL] url)))

(defmethod take-step :trip-created-time
  [state step]
  (let [t (t/trip-created-time (:z state))]
    (assoc-in state [:trip :responseData :tripCreatedTime] (-> t
                                                               time/date-from-str
                                                               time/time-map))))
(defmethod take-step :trip-updated-time
  [state step]
  (let [t (t/trip-update-time (:z state))]
    (assoc-in state [:trip :responseData :tripUpdatedTime] (-> t
                                                               time/date-from-str
                                                               time/time-map))))
(defmethod take-step :trip-cache-updated-time
  [state step]
  (assoc-in state
            [:trip :responseData :tripCacheUpdatedTime]
            (-> (t/trip-cache-update-time (:z state))
                time/date-from-str
                time/time-map)))

(defmethod take-step :rewards
  [state step]
  (assoc-in state [:trip :responseData :rewards]
            (array-map :totalPoints 328
                       :basePoints 298
                       :bonusPoints [(array-map :m_pointValue 30.00
                                                :m_pointType "offerPoint"
                                                :m_pointDescription "+silver Bonus Offer"
                                                :m_status "PENDING")]
                       :logoUrl "/static/default/default/images/myrewards/RewardsLogo_193x76.png"
                       :viewStatementURL "/user/rewards")))

(defn products [xs]
  (let [ps [t/hotels ::hotel
            t/flights ::air
            t/insurance ::insurance
            t/packages ::package
            t/legacy-packages ::package
            t/cars ::car
            t/legacy-activity ::legacy-activity
            t/external-cruises ::external-cruise
            t/activities ::activity
            t/cruises ::cruise]
        func (fn [[f p]] (when (seq (f xs)) p))]
    (set (remove nil? (map func (partition 2 ps))))))


(defn standalone-dx?
  "answers the question if this trip is standalone DX.
   collect product types, remove insurance,
   remaining set contains DX and size is 1"
  [xs]
  (let [p (products xs)
        p-prime (disj p ::insurance)]
    (and (= 1 (count p-prime)) (= ::activity (first p-prime)))))

(defn package-v2-dx?
  "answers the question if this trip has an activity
   booked with a package v2"
  [xs]
  (some
   #(re-find
     #"PACKAGE_V_2" (ap/activity-product-natural-key %))
     (t/activities xs)))

(defmethod take-step :activities
  [state step]
  (let [xs (-> (:z state) t/entities t/activities)
        ys (-> (:z state) t/entities)]
    (assoc-in state [:trip :responseData :activities]
              (mapv :activity
                    (remove nil? (map #(reduce (comp stop-if-error take-step)
                                               {:status :ok
                                                :host (:host state)
                                                :trip-id (t/trip-id (:z state))
                                                :itinerary-number (t/itinerary-number (:z state))
                                                :standalone-dx (standalone-dx? ys)
                                                :products (products ys)
                                                :package-v2-dx (package-v2-dx? ys)
                                                }
                                               (a/build-activity-steps {:z % :component-index %2})) ys (range (count ys))))))))

(defn booking-status
  "take coll with booking status of every component
   and return overall trip booking status
   if no booked items present, then SAVED
   if all pending -> pending
   if all cancelled -> cancelled
   if all failed -> failed
   else Booked"
  [xs]
  (cond
    ;(not-any? #(= % "BOOKED") xs) "SAVED"
    (every? #(= % "PENDING") xs)  "PENDING"
    (every? #(= % "CANCELLED") xs) "CANCELLED"
    (every? #(= % "FAILED") xs) "FAILED"
    :else "BOOKED")
  )

(defn component-booking-status [x]
  (let [product (->> x
                     ((juxt t/hotel? t/flight? t/insurance? t/package?
                            t/legacy-package?  t/car? t/legacy-activity?
                            t/external-cruise? t/activity? t/cruise?))
                     (remove nil?)
                     first)]
    (condp = product
      :hotel :unknown
      :flight :unknown
      :insurance :unknown
      :package :unknown
      :legacy-package :unknown
      :car :unknown
      :legacy-activity :unknown
      :external-cruise :unknown
      :activity (a/booking-status x)
      :cruise :unknown)))



(defmethod take-step :booking-status
  [state step]
  (let [xs (t/entities (:z state))
        statuses (map component-booking-status xs)]
    (assoc-in state [:trip :responseData :bookingStatus]
              (booking-status statuses))))

;{:step :activity-unbooked-package-url}
(defmethod take-step :read-trip-error
  [state step]
  (assoc-in state [:trip :responseData :readTripError] false))

(defmethod take-step :trip-source
  [state step]
  (assoc-in state [:trip :responseData :tripSource] "expedia"))

(defmethod take-step :is-mrp
  [state step]
  (assoc-in state [:trip :responseData :isMRP] false))

#_(defmethod take-step :trip-start-time
  [state step]
  (let [t (t/trip-start-time (:z state))]
    (assoc-in state [:trip :responseData :startTime] (-> t
                                                         time/date-from-str
                                                         time/time-map))))
#_(defmethod take-step :trip-end-time
  [state step]
  (let [t (t/trip-end-time (:z state))]
    (assoc-in state [:trip :responseData :endTime] (-> t
                                                         time/date-from-str

                                                         time/time-map))))

(defn run-with-circuit-breaker
  ([z] z)
  ([r v]
   (let [s (take-step r v)]
     (if (= :ok (get s :status))
       s
       (reduced s)))))

(defn execute-xf [steps]
  (let [result (reduce (comp stop-if-error take-step)
                       {:status :ok}
                       steps)
        {:keys [status]} result]
    (when (not= status :ok)
      (println "omg!"))
    result))

(defn execute-cb [steps]
  (let [result (reduce run-with-circuit-breaker
                       {:status :ok}
                       steps)
        {:keys [status]} result]
    (when (not= status :ok)
      (println "omg!"))
    result))

(defn execute [steps]
  (let [result (reduce take-step {:status :ok} steps)
        {:keys [status]} result]
    (when (not= status :ok)
      (println "omg!"))
    result))

(defn execute-debug [steps]
  (let [result (reductions take-step {:status :ok} steps)
        {:keys [trip]} result]
    (println trip)))
