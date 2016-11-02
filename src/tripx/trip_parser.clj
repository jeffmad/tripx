(ns tripx.trip-parser
  (:require [clojure.tools.logging :refer [log error warn debug]]
            [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip.xml :as zx]
            [tripx.xmlns :as x]))

(defn parse-xml
  "take the raw xml string response, parse it, and return a zipper fields."
  [s]
  (-> s
      java.io.StringReader.
      xml/parse
      zip/xml-zip))

(defn status-code [z]
  (zx/xml1-> z :xml58/ResponseInfo :xml58/ResponseStatus :xml58/StatusCode zx/text))

(defn trip-id [z]
  (zx/xml1-> z :xml01/Trip :xml01/TripKey :xml01/TripID zx/text))

(defn trip-number [z]
  (zx/xml1-> z :xml01/Trip :xml01/TripKey :xml01/TripNumber zx/text))

(defn itinerary-number [z]
  (zx/xml1-> z :xml01/Trip :xml01/TripKey :xml02/ItineraryID :xml02/ItineraryNumber zx/text))

(defn title [z]
  (zx/xml1-> z :xml01/Trip :xml01/Title zx/text))

(defn trip-created-time [z]
  (zx/xml1-> z :xml01/Trip :xml01/CreateDateTime zx/text))

(defn trip-update-time [z]
  (zx/xml1-> z :xml01/Trip :xml01/UpdateDateTime zx/text))

(defn trip-cache-update-time [z]
  ; does FromCache need to be checked?
  ;(zx/xml1-> z :xml20/ReadTripDataDetails :xml20/FromCache zx/text)
  (zx/xml1-> z :xml20/ReadTripDataDetails :xml20/LastUpdateDateTime zx/text))

(defn entities [z]
  (zx/xml-> z :xml01/Trip :xml01/EntityList :xml01/Entity))

(defn booked-air-product? [z]
  (zx/xml1-> z :xml10/AirProductEntity
             :xml10/AirItemEntity
             :xml10/AirBookedItemEntityList
             :xml10/AirBookedItemEntity))

(defn saved-air-product? [z]
  (zx/xml1-> z :xml10/AirProductEntity :xml19/AirNaturalKeyList))

(defn hotel? [z]
  (when (or (zx/xml1-> z :xml10/LodgingProductULPEntity)
            (zx/xml1-> z :xml10/LodgingProductEntity)) :hotel))

(defn flight? [z]
  (when (or (booked-air-product? z)
            (saved-air-product? z)) :flight))

(defn insurance? [z]
  (when (or (zx/xml1-> z :xml10/InsuranceProductEntity)
            (zx/xml1-> z :xml10/InsuranceProductEntityV2)) :insurance))

(defn package? [z]
  (when (zx/xml1-> z :xml10/PackageProductEntity) :package))

(defn legacy-package? [z]
  (when (zx/xml1-> z :xml10/LegacyPackageProductEntity
                   :xml10/PackageEntity) :legacy-package))

(defn car? [z]
  (when (zx/xml1-> z :xml10/CarProductEntity) :car))

(defn legacy-activity? [z]
  (when (zx/xml1-> z :xml10/ActivityProductEntity
                   :xml10/ActivityLegacyBookedItemEntityList
                   :xml10/ActivityLegacyBookedItem) :legacy-activity))

(defn external-cruise? [z]
  (when (zx/xml1-> z :xml10/ExternalCruiseEntity) :external-cruise))

(defn activity? [z]
  (when (zx/xml1-> z :xml10/DestinationExperienceProductEntity) :activity))

(defn cruise? [z]
  (when (zx/xml1-> z :xml10/CruiseProductEntity) :cruise))

(defn hotels [entities]
  (filter hotel? entities))

(defn flights [entities]
  (filter flight? entities))

(defn insurance [entities]
  (filter insurance?  entities))

(defn packages [entities]
  (filter package? entities))

(defn legacy-packages [entities]
  (filter legacy-package? entities))

(defn cars [entities]
  (filter car? entities))

(defn legacy-activity [entities]
  (filter legacy-activity? entities))

(defn external-cruises [entities]
  (filter external-cruise? entities))

(defn activities [entities]
  (filter activity? entities))

(defn cruises [entities]
  (filter cruise? entities))
