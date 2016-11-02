(ns tripx.trip-parser-test
  (:require [clojure.test :refer :all]
            [tripx.trip-parser :refer :all]))
(def file-based-trips ["activity1" "activity2" "car" "hotel"])

(defn build-file-name
  "take a trip number and add a prefix and suffix
   in order to look it up in the resources dir"
  [itin]
  (str "trips/xml/" itin ".xml"))

(defn read-trip-from-file
  "accept a relative filepath argument on the classpath
  ,then slurp the file up and parse the json into clojure"
  [file-name]
  (slurp (.toString (clojure.java.io/resource file-name))))


(deftest basic-trip-fields-test
  (testing "extract basic fields from xml"
    (let [z    (-> "activity1"
                   build-file-name
                   read-trip-from-file
                   parse-xml)
          mc (activity-modularized-content z)]
      (is (= "200" (status-code z)))
      (is (= "5D55D77C-E395-4CCD-B462-230658A47D9B" (trip-id z)))
      (is (= "7822024975" (trip-number z)))
      (is (= "11616646637" (itinerary-number z)))
      (is (= "New York, United States" (title z)))
      (is (= "2015-09-17T06:31:21.170Z" (trip-created-time z)))
      (is (= "2015-10-30T15:30:11.130Z" (trip-update-time z)))
      (is (= "2016-05-11T00:00:00" (activity-start-date z)))
      (is (= "2016-05-11T00:00:00" (activity-start-time z)))
      (is (= "2016-06-09T00:00:00" (activity-end-date z)))
      (is (= "2016-06-09T00:00:00" (activity-end-time z)))
      (is (= "1" (activity-booking-status z)))
      (is (= "1" (activity-booking-status-text z)))
      (is (= "2015-09-16T00:00:00.000-07:00" (activity-booking-date z)))
      (is (= "Merchant" (business-model z)))
      (is (= "Adult" (ticket-category-code z)))
      (is (= "1" (ticket-count z)))
      (is (= {:category-code "Price", :currency-code "USD", :price 149.0}
             (activity-price-component z "Price")))
      (is (= "2" (activity-category-id z)))
      (is (= "182983" (activity-id z)))
      (is (= "Explorer Pass: Choose 3, 5, 7 or 10 Museums, Tours & Attractions"
             (activity-title z)))
      (is (= "Explorer Pass - 7 Attractions & Tours"
             (activity-title-english z)))
      (is (= "Choose from over 55 of New York City's most amazing attractions, museums, and tours with this Explorer Pass and experience the city at your own pace. Enjoy access to famous locations as well as fast-track entry to some. Grab your camera and your walking shoes and immerse yourself in New York City. Use your pass & guidebook to craft your own itinerary and visit iconic locations throughout the town. Just a handful of the over 50 participating tours, museums, and attractions include the following, several of which offer front of the line privileges for Explorer Pass holders: - Top of the Rock Observation Decks - Empire State Building Observatory - Statue of Liberty & Ellis Island Ferry Ticket - 9/11 Tribute Center & Walking Tour - Hop-On Hop-Off Bus Tour by City Sights - Yankee Stadium: Classic Tour - Whitney Museum of American Art - Metropolitan Museum of Art (includes admission to the Cloisters) - American Museum of Natural History - Museum of Modern Art - Guggenheim Museum - Luna Park at Coney Island: 4-hour unlimited ride wristband - Saturday Night Live: The Exhibit - The Hunger Games: The Exhibition - Bike Tours and Rentals by Central Park Sightseeing - New York Water Taxi Hop On Hop Off All Day Pass - Circle Line Statue of Liberty Cruise - Intrepid Sea, Air & Space Museum - Radio City Music Hall Stage Door Tour - Rockefeller Center Tour - Bike & Roll: Guided Bike Tours & Rentals - Sex and the City Tour by On Location Tours - NY TV and Movie Sites Tour by On Location Tours - Woodbury Common Outlet Shopping Roundtrip Transportation - Hush Hip Hop Tours - The RIDE - The TOUR Best of all, your Explorer Pass is valid for 30 days from first activation. During this period, you may use it on any 3, 4, 5, 7 or 10 tours and attractions, depending which pass you purchase. This means you can truly explore and enjoy the city at your own pace no matter your length of stay in the Big Apple."
             (activity-description z)))
      (is (= [{:address-line1 "56 West 56th Street, near 6th Avenue",
  :city "New York",
  :country-code "USA",
  :full-address "Central Park Sightseeing : 56 West 56th Street, near 6th Avenue, New York, New York, 10019, USA",
  :latitude 40.76327,
  :location-name "Central Park Sightseeing",
  :longitude -73.97713,
  :postal-code "10019",
  :province-name "New York"}
 {:address-line1 "1540 Broadway at 45th Street",
  :city "New York",
  :country-code "USA",
  :full-address "Planet Hollywood - CitySights NY Desk : 1540 Broadway at 45th Street, New York, NY, 10036, USA",
  :latitude 40.75815,
  :location-name "Planet Hollywood - CitySights NY Desk",
  :longitude -73.98505,
  :postal-code "10036",
  :province-name "NY"}
 {:address-line1 "226 West 44th Street, near Broadway",
  :city "New York",
  :country-code "USA",
  :full-address "Discovery Times Square : 226 West 44th Street, near Broadway, New York, NY, 10036, USA",
  :latitude 40.75766,
  :location-name "Discovery Times Square",
  :longitude -73.98711,
  :postal-code "10036",
  :province-name "NY"}
 {:address-line1 "110 South Street near South Street Seaport",
  :city "New York",
  :country-code "USA",
  :full-address "Brooklyn Bridge Sightseeing : 110 South Street near South Street Seaport, New York, New York, 10038, USA",
  :latitude 40.70543,
  :location-name "Brooklyn Bridge Sightseeing",
  :longitude -74.00288,
  :postal-code "10038",
  :province-name "New York"}
 {:address-line1 "Circle Downtown Booth 89 South Street Pier 16, South Street Seaport",
  :city "New York",
  :country-code "USA",
  :full-address "New York Water Taxi/Circle Line Downtown : Circle Downtown Booth 89 South Street Pier 16, South Street Seaport, New York, New York, 10038, USA",
  :latitude 40.70715,
  :location-name "New York Water Taxi/Circle Line Downtown",
  :longitude -74.0021,
  :postal-code "10038",
  :province-name "New York"}]
             (redemption-locations z)))
      (is (= {:address-line1 "56 West 56th Street, near 6th Avenue",
              :city "New York",
              :country-code "USA",
              :full-address "Central Park Sightseeing : 56 West 56th Street, near 6th Avenue, New York, New York, 10019, USA",
              :latitude 40.76327,
              :location-name "Central Park Sightseeing",
              :longitude -73.97713,
              :postal-code "10019",
              :province-name "New York"} (activity-location z)))
      (is (= 1 (traveler-count z)))
      (is (= [{:is-redeemer "true", :first-name "Elmer", :last-name "Fudd", :full-name "Elmer  Fudd"}]
             (activity-travelers z)))
      (is (= [{:day "monday", :start "09:00:00", :end "20:00:00"} {:day "tuesday", :start "09:00:00", :end "20:00:00"} {:day "wednesday", :start "09:00:00", :end "20:00:00"} {:day "thursday", :start "09:00:00", :end "20:00:00"} {:day "friday", :start "09:00:00", :end "20:00:00"} {:day "saturday", :start "08:00:00", :end "20:00:00"} {:day "sunday", :start "08:00:00", :end "20:00:00"}] (-> z hours-of-operation)))
      (is (= {:title "Redemption Instructions", :body "Upon arrival at one of the redemption locations, please present your confirmation/voucher and photo ID in exchange for your activity.", :display-format "plaintext"}
             (activity-modularized-content-type mc "redemption_instructionsGroup")))
      (is (= {:title "Redemption Instructions", :body "Upon arrival at one of the redemption locations, please present your confirmation/voucher and photo ID in exchange for your activity.", :display-format "plaintext"}
             (redemption-instructions z)))
      (is (= {:title "Know Before You Go", :body "Hours of operation vary; please check before you go.||Advance reservations may be made for tours included on the pass; mention you are a New York Explorer Card holder.||Your Explorer Pass is valid for 30 days from first activation.||**Save 15% on Central Park Carriage Rides and 20% on Pedicab Rides valid for redemptions made through August 31, 2015 at Central Park Sightseeing.**", :display-format "list"}
             (know-before-you-go z)))
      (is (= "0046461841" (voucher-bar-code z)))
      (is (= "109121010001490708568" (voucher-security-code z)))
      (is (= "CA4B2A4E-A613-4314-AE1C-2DD2C92F28FD" (entity-id z)))
      (is (= "8016977349905" (order-number z)))
      (is (= "a8070f85-05e6-4e5c-bb98-09c045c9c22d" (order-line-guid z)))
      )))
