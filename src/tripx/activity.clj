(ns tripx.activity
  (:require [tripx.activity-parser :as p]
            [tripx.time :as t]
            [tripx.step :refer [take-step]]))


(defn build-activity-steps [req]
  [{:step :activity-start
    :activity-entity (:z req)}
   {:step :activity-unique-id
    :component-index (:component-index req)}
   {:step :activity-booking-status}
   ;{:step :activity-booking-status-text}
   ;{:step :activity-booking-status-message}
   {:step :activity-supplier-reference-number}
   {:step :activity-booking-date}
   {:step :activity-start-time}
   {:step :activity-end-time}
   {:step :activity-payment-model}
   ;{:step :activity-price}
   {:step :activity-category-id}
   {:step :activity-image}
   {:step :activity-hires-image}
   {:step :activity-id}
   {:step :activity-title}
   {:step :activity-item-title}
   {:step :activity-desc}
   {:step :activity-title-english}
   {:step :activity-details-url}
   {:step :activity-location}
   {:step :activity-redemption-locations}
   {:step :activity-traveler-count}
   {:step :activity-voucherless}
   ;{:step :activity-traveler-count-desc}
   {:step :activity-travelers}
   {:step :activity-vendor-name}
   {:step :activity-vendor-accounting-id}
   {:step :activity-vendor-cust-service}
   {:step :activity-t-and-c}
   {:step :activity-voucher-print-url}
   {:step :activity-vouchers}
   {:step :activity-cancel-url}
   {:step :activity-region-id}
   {:step :activity-unbooked-package-v2}
   ;{:step :activity-unbooked-package-url}
   ])

(defmethod take-step :activity-start
  [state step]
  (let [z (:activity-entity step)
        v2 (and (p/booked? z) (or (:standalone-dx state)
                                  (:package-v2-dx state)))
        ;_ (println "booked " (p/booked? z))
        ;_ (println "p " (:products state))
        ;_ (println "sa " (:standalone-dx state))
        ;_ (println "pv2 " (:package-v2-dx state))
        ]
    ; saved activity not supported here yet- must be booked.
    ; legacy activity not supported only v2
    (if v2
      (assoc
       state :z z)
      (reduced state))))

(defmethod take-step :activity-unique-id
  [state step]
  (let [entity-id (p/entity-id (:z state))
        activity-id (p/activity-id (:z state))
        activity-item-id (p/activity-item-id (:z state))
        i (:component-index step)]
    (assoc state :activity
           (array-map :uniqueID
                      (apply str (interpose "_"
                                            [entity-id
                                             i
                                             activity-id
                                             activity-item-id]))))))

(defn booking-status-code [s]
  (condp = s
    "1" "BOOKED"
    "2" "CANCELLED"
    "3" "PENDING"
    :else "UNKNOWN"))

(defn booking-status [z]
  (booking-status-code (p/activity-booking-status z)))

(defmethod take-step :activity-booking-status
  [state step]
  (assoc-in state [:activity :bookingStatus]
            (booking-status (:z state))))

(defmethod take-step :activity-booking-date
  [state step]
  (assoc-in state [:activity :bookingDate]
            (-> (p/activity-booking-date (:z state))
                t/date-from-str
                t/time-map)))

(defmethod take-step :activity-start-time
  [state step]
  (assoc-in state [:activity :startTime] (-> (p/activity-start-date (:z state))
                                              t/date-from-str
                                              t/time-map)))

(defmethod take-step :activity-end-time
  [state step]
  (assoc-in state [:activity :endTime] (-> (p/activity-end-date (:z state))
                                            t/date-from-str
                                            t/time-map)))

(defmethod take-step :activity-payment-model
  [state step]
  (assoc-in state [:activity :paymentModel]
            (-> (p/activity-business-model (:z state)))))

;{:step :activity-price}

(defmethod take-step :activity-category-id
  [state step]
  (assoc-in state [:activity :activityCategoryID]
            (p/activity-category-id (:z state))))

(defmethod take-step :activity-id
  [state step]
  (assoc-in state [:activity :activityId] (p/activity-id (:z state))))

(defmethod take-step :activity-title
  [state step]
  (assoc-in state [:activity :activityTitle] (p/activity-title (:z state))))

(defmethod take-step :activity-unbooked-package-v2
  [state step] ; if not booked and packageV2 true, set true
  (assoc-in state [:activity :unbookedPackageV2] false))

(defmethod take-step :activity-voucherless
  [state step]
  (assoc-in state [:activity :voucherless]
            (true? (some->> (p/activity-product-natural-key (:z state))
                             (re-find #"Voucherless")))))

(defmethod take-step :activity-item-title
  [state step]
  (if-let [a (p/activity-item-title (:z state))]
    (assoc-in state [:activity :activityItemTitle] a)))

(defmethod take-step :activity-image
  [state step]
  (if-let [i (p/activity-image (:z state))]
    (assoc-in state [:activity :image] { :url (:url i)
                                        :caption (or (:caption i) "")
                                        :creditString (or (:credit i) "")})))
(defmethod take-step :activity-hires-image
  [state step]
  (if-let [i (p/activity-hires-image (:z state) 178 359)]
    (assoc-in state [:activity :highResImage] { :url (:url i)
                                               :height (:height i)
                                               :width (:width i)
                                        :caption (or (:caption i) "")
                                        :creditString (or (:credit i) "")})))

(defmethod take-step :activity-desc
  [state step]
  (assoc-in state [:activity :activityDescription] (p/activity-description (:z state))))

(defmethod take-step :activity-title-english
  [state step]
  (assoc-in state [:activity :activityItemTitleEnglish]
            (p/activity-title-english (:z state))))

(defmethod take-step :activity-details-url
  [state step]
  (assoc-in state [:activity :activityDetailsURL]
            (str "https://"
                 (:host step)
                 "/lx/activity/" (p/activity-id (:z state)))))

(defn map->trip-json-location [location]
  (array-map :name1 (:location-name location)
             :latitude (:latitude location)
             :longitude (:longitude location)
             :addressLine1 (:address-line1 location)
             :city (:city location)
             :countrySubdivisionCode (:province location)
             :postalCode (:postal-code location)
             :countryCode (:country-code location)
             :fullAddress (:full-address location)))

(defmethod take-step :activity-location
  [state step]
  (assoc-in state [:activity :activityLocation]
            (map->trip-json-location
             (first (p/activity-redemption-locations (:z state))))))

(defmethod take-step :activity-redemption-locations
  [state step]
  (assoc-in state [:activity :redemptionLocations]
            (mapv map->trip-json-location
                  (p/activity-redemption-locations (:z state)))))

(defmethod take-step :activity-traveler-count
  [state step]
  (assoc-in state [:activity :travelerCount] (p/activity-traveler-count (:z state))))

                                        ;{:step :activity-traveler-count-desc}

(defn m->traveler [m]
  (array-map :isRedeemer (:is-redeemer m)
             :firstName  (:first-name m)
             :lastName (:last-name m)
             :fullName (:full-name m)))

(defmethod take-step :activity-travelers
  [state step]
  (assoc-in state [:activity :travelers]
            (mapv m->traveler (p/activity-travelers (:z state)))))

(defmethod take-step :activity-vendor-name
  [state step]
  (assoc-in state [:activity :vendorName] (p/activity-vendor-name (:z state))))

(defmethod take-step :activity-vendor-accounting-id
  [state step]
  (assoc-in state [:activity :vendorAccountingId]
            (p/activity-vendor-accounting-id (:z state))))
                                        ;{:step :activity-vendor-cust-service}
(defn know-before-you-go [z]
  (let [know (p/know-before-you-go z)
        know-title (:title know)
        know-body (:body know)
        know-body2 (clojure.string/replace know-body #"[|]{2}" "</li><li>")]
    {:title know-title :body know-body2}))

(defn know-before [z]
  (let [know (know-before-you-go z)]
    (str "<b>" (:title know) ":</b><ul><li>" (:body know) "</li></ul>")))

(defn how-to-redeem [z]
  (let [instr (:body (p/redemption-instructions z))
        msg "How to Redeem Your Voucher"]
    (str "<b>" msg ":</b><br/>" instr)))

(defn instructions [z]
  (let [instr (how-to-redeem z)
        know (know-before-you-go z)
        know-title (:title know)
        know-body (:body know)]
    ; TODO check for empty content, substitute out pipes
    (str instr "<br/><br/>" "<b>" know-title ":</b><ul><li>" know-body "</li></ul>")))

(defn inclusions [z]
  (let [incl (p/inclusions z)
        title (:title incl)
        body (:body incl)
        body2 (clojure.string/replace body #"[|]{2}" "</li><li>")]
    (str "<b>" title ":</b><ul><li>" body2 "</li></ul>")))

(defn cancellation-addendum [z]
  (:body (p/cancellation-addendum z)))

(defn cancel-policy [r v]
  (let [fee (:fee v)
        percent (:percent-back v)
        hours (:min-hours v)
        able-to-cancel (and (zero? fee) (not (neg? hours)) (= 100 percent))
        h (:cancel-window-hours r)]
    (assoc r :cancelable? able-to-cancel
             :cancel-window-hours (if able-to-cancel (min hours h) ))))

(defn summarize-cancel [xs]
  (reduce cancel-policy {:cancel-window-hours Integer/MAX_VALUE
                         :cancelable? nil} xs))

(defn cancel-messages []
  { :msg-ok "You can cancel free of charge until %s hours before the activity starts. After that time, no cancellations, changes or refunds will be made."
   :msg-no "This activity is non-refundable and cannot be changed or cancelled after booking."})

(defn cancellation-plain [z]
  (let [policy (summarize-cancel (p/cancel-policies z))]
    (if (:cancelable? policy)
      (format (:msg-ok (cancel-messages)) (:cancel-window-hours policy))
      (:msg-no (cancel-messages)))))

(defn cancellation [z]
  (let [h "Cancellations and Changes"
        policy (summarize-cancel (p/cancel-policies z))
        msgs (cancel-messages)
        msg-ok (format (:msg-ok msgs) (:cancel-window-hours policy))
        msg-no (:msg-no msgs)
        text (if (:cancelable? policy) msg-ok msg-no)]
    (str "<li><b>" h ":</b>" text "</li>")))



(defmethod take-step :activity-t-and-c
  [state step]
  (assoc-in state [:activity :termsAndConditions]
            (array-map :instructions (instructions (:z state))
                       :knowBeforeYouGo (know-before (:z state))
                       :howToRedeem (how-to-redeem (:z state))
                       :terms (cancellation (:z state))
                       :inclusions (inclusions (:z state))
                       :minFreeCancellationHoursPackageV2 0
                       :freeCancellationAvailablePackageV2 false
                       :responsiveHowToUseDealText
                         (:body (p/redemption-instructions (:z state)))
                       :responsiveKnowBeforeYouGoText
                       (clojure.string/split
                        (:body (p/know-before-you-go (:z state))) #"[|]{2}")
                       :responsiveCancellationAddendumText
                         (cancellation-addendum (:z state))
                       :responsiveTerms (:body (p/terms (:z state)))
                       :responsiveCancelPolicy (cancellation-plain (:z state)))))

                                        ;{:step :activity-t-and-c}

;"take the zipper from root so we can get tripId and itin number, or put it in state"
(defmethod take-step :activity-voucher-print-url
  [state step]
  (assoc-in state [:activity :voucherPrintURL]
            (str "https://"
                 (:host state)
                 "/itinerary-print?tripid="
                 (:trip-id state) "&itineraryNumber=" (:itinerary-number state))))

(defmethod take-step :activity-vouchers
  [state step]
  (assoc-in state [:activity :vouchers]
            [(array-map :id ""
                        :barcodeNumber (p/voucher-bar-code (:z state))
                        :securityCode (p/voucher-security-code (:z state))
                        :barcodeImageURL ""
                        :validForRedemerTypeLocalized "1 Adult"
                        :validForRedemerTypeEnglish "1 Adult"
                        :supplierReferenceCode "")]))

(defmethod take-step :activity-category-name
  [state step]
  (if-let [t (p/activity-category-name (:z state))]
    (assoc-in state [:activity :activityItemTitle])
    state))

(defmethod take-step :activity-supplier-reference-number
  [state step]
  (let [n (or (p/supplier-reference-number (:z state)) (:itinerary-number state))]
    (assoc-in state [:activity :supplierReferenceNumber] n)))

(defmethod take-step :activity-vendor-cust-service
  [state step]
  (assoc-in state [:activity :vendorCustomerServiceOffices]
            (p/activity-vendor-cust-service-offices (:z state)))

  )
;"take the zipper from root so we can get tripId and itin number, or put it in state"
(defmethod take-step :activity-cancel-url
  [state step]
  (assoc-in state [:activity :webGetLXCancelDetailPathURL]
            (str "https://"
                 (:host state)
                 "/api/getLXCancelDetail/"
                 (:trip-id state)
                 "/ordernumber/"
                 (p/order-number (:z state))
                 "/orderlinenumber/"
                 (p/order-line-guid (:z state)))))

;"take the lat/lon of first redemption location and call geography service to get region"
(defmethod take-step :activity-region-id
  [state step]
  (assoc-in state [:activity :regionID] "2621"))
