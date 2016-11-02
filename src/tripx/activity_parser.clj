(ns tripx.activity-parser
  (:require [clojure.tools.logging :refer [log error warn debug]]
            [clojure.data.zip.xml :as zx]))

(defn booked? [z]
  (boolean (zx/xml1-> z :xml10/DestinationExperienceProductEntity
                      :xml10/DestinationExperienceItemEntity
                      :xml10/DestinationExperienceBookedItemEntityList
                      :xml10/DestinationExperienceBookedItemEntity)))

(defn activity-start-date [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
               :xml30/ActivityStartDateTimeInformation :xml05/Date  zx/text))

(defn activity-start-time [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
               :xml30/ActivityStartDateTimeInformation :xml05/Time  zx/text))

(defn activity-end-date [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
               :xml30/ActivityEndDateTimeInformation :xml05/Date  zx/text))

(defn activity-end-time [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
               :xml30/ActivityEndDateTimeInformation :xml05/Time  zx/text))

(defn activity-booking-status [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/OrderTransactionState zx/text))

(defn activity-booking-status-text [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData :xml30/BookingItemStateID zx/text))

(defn activity-booking-date [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData
             :xml30/VoucherInformationData
             :xml30/BookingDate zx/text))

(defn activity-business-model [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData :xml30/BusinessModel zx/text))
; ticket category code "Adult"
(defn activity-ticket-category-code [z]
  (zx/xml1-> z :xml01/Trip :xml01/EntityList :xml01/Entity
             :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData :xml30/TotalTicketPriceByCategory
             :xml30/TicketCategoryCode zx/text))

(defn activity-ticket-count [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData
             :xml30/TotalTicketPriceByCategory
             :xml30/TicketCount zx/text))

(defn activity-price-component [z category-code]
  (let [prices (zx/xml-> z :xml10/DestinationExperienceProductEntity
                         :xml10/DestinationExperienceItemEntity
                         :xml10/DestinationExperienceBookedItemEntityList
                         :xml10/DestinationExperienceBookedItemEntity
                         :xml30/EdxItemData
                         :xml30/TotalTicketPriceByCategory
                         :xml30/PriceList
                         :xml30/Price)
        price-per (first
                   (filter
                    #(zx/xml1-> % (zx/attr= :xml16/FinanceCategoryCode category-code))
                      prices))
        price-int (zx/attr price-per :xml03/Decimal)
        price-dec (zx/attr price-per :xml03/DecimalPlaceCount)]
    {:category-code category-code
     :price (/ (Integer/parseInt price-int)
               (java.lang.Math/pow 10 (Integer/parseInt price-dec)))
     :currency-code (zx/attr price-per :xml16/CurrencyCode)
     }))

(defn activity-category-id [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData
             :xml30/Activity
             :xml30/ActivityCategory
             :xml30/ActivityCategoryID zx/text))

(defn activity-category-name [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData
             :xml30/Activity
             :xml30/ActivityCategory
             :xml30/ActivityCategoryName zx/text))

(defn activity-id [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData :xml30/Activity :xml30/ActivityID zx/text))

(defn activity-item-id [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData :xml30/ActivityItem
             :xml30/ActivityItemID zx/text))

(defn activity-title [z]
  (str  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
                   :xml10/DestinationExperienceItemEntity
                   :xml10/DestinationExperienceBookedItemEntityList
                   :xml10/DestinationExperienceBookedItemEntity
                   :xml30/EdxItemData
                   :xml30/Activity
                   :xml30/ActivityTitleEnglish zx/text)
        ": "
        (zx/xml1-> z :xml10/DestinationExperienceProductEntity
                   :xml10/DestinationExperienceItemEntity
                   :xml10/DestinationExperienceBookedItemEntityList
                   :xml10/DestinationExperienceBookedItemEntity
                   :xml30/EdxItemData :xml30/ActivityItem
                   :xml30/ActivityItemTitleEnglish zx/text)))

(defn activity-item-title [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData
             :xml30/Activity
             :xml30/ActivityCategory
             :xml30/ActivityCategoryName zx/text))

(defn activity-product-natural-key [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml30/ProductNaturalKey zx/text))

(defn activity-image [z]
  (let [i (zx/xml1-> z :xml10/DestinationExperienceProductEntity
                     :xml10/DestinationExperienceItemEntity
                     :xml10/DestinationExperienceBookedItemEntityList
                     :xml10/DestinationExperienceBookedItemEntity
                     :xml30/EdxItemData :xml30/Activity :xml30/Image)]
    { :url (zx/xml1-> i :xml30/FileURL zx/text)
      :height (zx/xml1-> i :xml30/ImageSize :xml30/Height zx/text)
      :width (zx/xml1-> i :xml30/ImageSize :xml30/Width zx/text)
      :caption (zx/xml1-> i :xml30/Caption zx/text)
      :credit (zx/xml1-> i :xml30/CreditString zx/text)
     }))

(defn activity-hires-image [z min-height min-width]
  (let [xs (zx/xml-> z :xml10/DestinationExperienceProductEntity
                     :xml10/DestinationExperienceItemEntity
                     :xml10/DestinationExperienceBookedItemEntityList
                     :xml10/DestinationExperienceBookedItemEntity
                     :xml30/EdxItemData :xml30/Activity
                     :xml30/ImageList :xml30/Image)
        big-enough? (fn [h w] (and (< min-height h) (< min-width w)))
        i (some #(when (big-enough?
                        (Integer/parseInt (zx/xml1-> % :xml30/ImageSize
                                                     :xml30/Height zx/text))
                        (Integer/parseInt (zx/xml1-> % :xml30/ImageSize
                                                     :xml30/Width zx/text))) %) xs)]
    { :url (zx/xml1-> i :xml30/FileURL zx/text)
     :height (zx/xml1-> i :xml30/ImageSize :xml30/Height zx/text)
     :width (zx/xml1-> i :xml30/ImageSize :xml30/Width zx/text)
     :caption (zx/xml1-> i :xml30/Caption zx/text)
     :credit (zx/xml1-> i :xml30/CreditString zx/text)
     }))

(defn activity-title-english [z]
  (activity-title z))

(defn activity-description [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData :xml30/Activity
             :xml30/ActivityDescription zx/text))

(defn full-address [name street city province postal-code country]
  (str name " : " street ", " city ", " province ", " postal-code ", " country))

(defn activity-redemption-location [location]
  (let [loc-name    (zx/xml1-> location :xml30/LocationName zx/text)
        street      (zx/xml1-> location :xml07/Address :xml07/FirstAddressLine zx/text)
        city        (zx/xml1-> location :xml07/Address :xml07/CityName zx/text)
        province    (zx/xml1-> location :xml07/Address :xml07/ProvinceName zx/text)
        postal-code (zx/xml1-> location :xml07/Address :xml07/PostalCode zx/text)
        country     (zx/xml1-> location :xml07/Address :xml07/CountryAlpha3Code zx/text)
        lat-decimal (zx/xml1-> location :xml07/LatLong
                               :xml07/LatitudeAmount
                               (zx/attr :xml03/DecimalPlaceCount))
        lon-decimal (zx/xml1-> location :xml07/LatLong
                               :xml07/LongitudeAmount
                               (zx/attr :xml03/DecimalPlaceCount))
        lat         (zx/xml1-> location :xml07/LatLong
                               :xml07/LatitudeAmount :xml03/Decimal zx/text)
        lon         (zx/xml1-> location :xml07/LatLong
                               :xml07/LongitudeAmount :xml03/Decimal zx/text)]
    {
     :location-name loc-name
     :latitude (/ (Integer/parseInt lat)
                  (java.lang.Math/pow 10.0 (Integer/parseInt lat-decimal)))
     :longitude (/ (Integer/parseInt lon)
                   (java.lang.Math/pow 10.0 (Integer/parseInt lon-decimal)))
     :address-line1 street
     :city city
     :province province
     :postal-code postal-code
     :country-code country
     :full-address (full-address loc-name street city province postal-code country)
     }))

(defn activity-redemption-locations [z]
  (let [locations (zx/xml-> z :xml10/DestinationExperienceProductEntity
                            :xml10/DestinationExperienceItemEntity
                            :xml10/DestinationExperienceBookedItemEntityList
                            :xml10/DestinationExperienceBookedItemEntity
                            :xml30/EdxItemData :xml30/VoucherInformationData
                            :xml30/VoucherRedemptionInfo
                            :xml30/VoucherRedemptionLocationList
                            :xml30/VoucherRedemptionLocation)]
    (mapv activity-redemption-location locations)))

(defn activity-location [z]
  (first (activity-redemption-locations z)))

(defn activity-traveler-count [z]
  (count (zx/xml-> z :xml10/DestinationExperienceProductEntity
                   :xml10/DestinationExperienceItemEntity
                   :xml10/DestinationExperienceBookedItemEntityList
                   :xml10/DestinationExperienceBookedItemEntity
                   :xml30/EdxItemData
                   :xml30/TravelerInformation
                   :xml30/Traveler)))

(defn activity-traveler [t]
  (let [first-name (zx/xml1-> t :xml22/Traveler
                              :xml09/Person
                              :xml09/PersonName
                              :xml09/FirstName zx/text)
        middle-name (zx/xml1-> t :xml22/Traveler
                               :xml09/Person
                               :xml09/PersonName
                               :xml09/MiddleName zx/text)
        last-name (zx/xml1-> t :xml22/Traveler
                             :xml09/Person
                             :xml09/PersonName
                             :xml09/LastName zx/text)]
    (array-map :is-redeemer (zx/xml1-> t :xml30/IsRedeemer zx/text)
               :first-name  first-name
               :last-name   last-name
               :full-name   (str first-name " " middle-name " " last-name))))

(defn activity-travelers [z]
  (let [xs (zx/xml-> z :xml10/DestinationExperienceProductEntity
                     :xml10/DestinationExperienceItemEntity
                     :xml10/DestinationExperienceBookedItemEntityList
                     :xml10/DestinationExperienceBookedItemEntity
                     :xml30/EdxItemData
                     :xml30/TravelerInformation
                     :xml30/Traveler)]
    (mapv activity-traveler xs)))

(defn daily-hours [hoo]
  {:day (zx/xml1-> hoo :xml30/DayOfWeek zx/text)
   :startTime (zx/xml1-> hoo :xml30/StartTime zx/text)
   :endTime (zx/xml1-> hoo :xml30/EndTime zx/text)})

(defn hours-of-operation [z]
  (mapv daily-hours z))

(defn build-phone-number [z]
  (let [country-code (zx/xml1-> z :xml07/PhoneCountryCode zx/text)
        area (zx/xml1-> z :xml07/PhoneAreaCode zx/text)
        number (zx/xml1-> z :xml07/PhoneNumber zx/text)]
    (str country-code " (" area ") " number )))

(defn build-phone-map [z]
  {:formatted (build-phone-number z)
   :costText "Toll-free"
   :useText "Business Hours"
   })

(defn short-name [d]
  (condp = d
      "sunday" "Sun"
      "monday" "Mon"
      "tuesday" "Tue"
      "wednesday" "Wed"
      "thursday" "Thu"
      "friday" "Fri"
      "saturday" "Sat"
      d))

(defn format-days [days]
  (->> days
       (map short-name)
       (interpose ", ")
       (apply str)))

(defn format-group [[hrs days]]
  (str (format-days days) ": " hrs))

(defn hours-of-operation-text
  "take in a map with keys :day, :startTime, :endTime.
   return a string with hours of operation grouped like
   Mon, Tues, Wed: 9:00AM - 5:00PM; Thu, Fri: 1:00PM - 2:00PM"
  [h]
  (->> h
       (map #(vector (:day %) (str (:startTime %) "-" (:endTime %))))
       (group-by second)
       (map (fn [[k v]] [k (map first v)]))
       (map format-group)
       (interpose "; ")
       (apply str)
       ))

(defn vendor-cust-service-office [z]
  (let [h (hours-of-operation
           (zx/xml-> z :xml30/HoursOfOperationList :xml30/HoursOfOperation)) ]
    {
     :name (zx/xml1-> z :xml30/LocationName zx/text)
     :phoneCountryCode (zx/xml1-> z :xml07/PhoneList
                                  :xml07/Phone
                                  :xml07/PhoneCountryCode zx/text)
     :phoneNumber (build-phone-number (zx/xml1-> z :xml07/PhoneList :xml07/Phone))
     :phoneNumbers (mapv build-phone-map (zx/xml-> z :xml07/PhoneList :xml07/Phone))
     :hourOfOperation h
     :hoursOfOperationText (hours-of-operation-text h)
     }))

(defn activity-vendor-cust-service-offices [z]
  (let [offices (zx/xml-> z :xml10/DestinationExperienceProductEntity
                          :xml10/DestinationExperienceItemEntity
                          :xml10/DestinationExperienceBookedItemEntityList
                          :xml10/DestinationExperienceBookedItemEntity
                          :xml30/EdxItemData :xml30/VoucherInformationData
                          :xml30/VoucherRedemptionInfo
                          :xml30/SupplierCustomerServiceLocationList
                          :xml30/SupplierCustomerServiceLocation)]
    (mapv  vendor-cust-service-office offices)))

(defn activity-vendor-name [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity :xml30/EdxItemData
             :xml30/VoucherInformationData :xml30/VendorName zx/text))

(defn activity-vendor-accounting-id [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml30/EdxItemData
             :xml30/VoucherInformationData :xml30/VendorBranchId zx/text))
;:xml01/Trip :xml01/EntityList :xml01/Entity

(defn activity-modularized-content [z]
  (zx/xml-> z :xml10/DestinationExperienceProductEntity
            :xml10/DestinationExperienceItemEntity
            :xml10/DestinationExperienceBookedItemEntityList
            :xml10/DestinationExperienceBookedItemEntity
            :xml30/EdxItemData :xml30/VoucherInformationData
            :xml30/ModularizedContentList :xml30/ModularizedContent))

(defn cancel-policy [p]
  (let [h (zx/xml1-> p :MinHours zx/text)
        hours (if h (Integer/parseInt h) Integer/MAX_VALUE)
        pct (zx/xml1-> p :PercentBack zx/text)
        percent (if p (Integer/parseInt pct) 0)
        f (zx/xml1-> p :Fee zx/text)
        fee (if p (Integer/parseInt f) 0)
        ]
    {:min-hours hours
     :percent-back percent
     :fee fee
     }))

(defn cancel-policies [z]
  (let [policies (zx/xml-> z :xml10/DestinationExperienceProductEntity
                           :xml10/DestinationExperienceItemEntity
                           :xml10/DestinationExperienceBookedItemEntityList
                           :xml10/DestinationExperienceBookedItemEntity
                           :xml30/EdxItemData :xml30/VoucherInformationData
                           :xml30/CancelPolicyList :xml30/CancelPolicy)]
    (mapv cancel-policy policies)))

(defn activity-modularized-content-type
  "TODO: also check for display format"
  [mc content-type]
  (let [n (first (filter #(zx/xml1-> % :ContentType content-type) mc))]
    {:title (zx/xml1-> n :Title zx/text)  ; for some reason these are not namespaced
     :body (zx/xml1-> n :Body zx/text)
     :display-format (zx/xml1-> n :DisplayFormat zx/text)}))

(defn supplier-reference-number [z]
  (let [mc (activity-modularized-content z)
        n (first (filter #(zx/xml1-> % :Title "VoucherRedemptionDisplayID") mc))]
    (if n (zx/xml1-> n :Body zx/text))))

(defn redemption-instructions [z]
  (let [mc (activity-modularized-content z)]
    (activity-modularized-content-type mc "redemption_instructionsGroup")))

(defn know-before-you-go [z]
  (let [mc (activity-modularized-content z)]
    (activity-modularized-content-type mc "know_before_you_goGroup")))

(defn inclusions [z]
  (let [mc (activity-modularized-content z)]
    (activity-modularized-content-type mc "inclusionsGroup")))

(defn terms [z]
  (let [mc (activity-modularized-content z)]
    (activity-modularized-content-type mc "terms_and_conditionsGroup")))

(defn cancellation-addendum [z]
  (let [mc (activity-modularized-content z)]
    (activity-modularized-content-type mc "cancellation_addendumGroup")))

(defn voucher-bar-code [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity :xml30/EdxItemData
             :xml30/VoucherInformationData :xml30/VoucherBarCode zx/text))

(defn voucher-security-code [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity :xml30/EdxItemData
             :xml30/VoucherSecurityCode zx/text))

(defn entity-id [z]
  (zx/xml1-> z :xml01/EntityID zx/text))

(defn order-number [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity :xml10/OrderNumber zx/text))

(defn order-line-guid [z]
  (zx/xml1-> z :xml10/DestinationExperienceProductEntity
             :xml10/DestinationExperienceItemEntity
             :xml10/DestinationExperienceBookedItemEntityList
             :xml10/DestinationExperienceBookedItemEntity
             :xml10/OrderLineGUID zx/text))
