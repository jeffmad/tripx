(ns tripx.xmlns
  (:require [clojure.data.xml :as xml]))
; ns4="urn:expedia:e3:data:basetypes:defn:v4"
; ns17="urn:expedia:e3:data:financetypes:defn:v4"
; xml namespaces are evil. in order to read attributes we need these declarations
; read more https://github.com/clojure/data.xml

(xml/declare-ns "xml01" "urn:expedia:e3:ews:core:trip:entities:defn:v1")
(xml/declare-ns "xml02" "urn:expedia:e3:ews:core:itinerary:commontypes:defn:v1")
(xml/declare-ns "xml03" "urn:expedia:e3:data:basetypes:defn:v4")
(xml/declare-ns "xml04" "urn:expedia:e3:ss:lodging:lodginginterface:commontypes:defn:v3")
(xml/declare-ns "xml05" "urn:expedia:e3:data:timetypes:defn:v4")
(xml/declare-ns "xml06" "urn:expedia:e3:ss:lodging:lodginginterface:bookingcommontypes:defn:v1")
(xml/declare-ns "xml07" "urn:expedia:e3:data:placetypes:defn:v4")
(xml/declare-ns "xml08" "urn:expedia:e3:ss:lodging:lodginginterface:messages:availdetailtypes:defn:v2")
(xml/declare-ns "xml09" "urn:expedia:e3:data:persontypes:defn:v4")
(xml/declare-ns "xml10" "urn:expedia:e3:ews:core:trip:entities:defn:v2")
(xml/declare-ns "xml11" "urn:expedia:e3:ss:lodging:lodginginterface:messages:retrievetypes:defn:v1")
(xml/declare-ns "xml12" "urn:expedia:e3:ss:lodging:lodginginterface:commontypes:defn:v4")
(xml/declare-ns "xml13" "urn:expedia:e3:ss:lodging:lodginginterface:bookingcommontypes:defn:v2")
(xml/declare-ns "xml14" "urn:expedia:e3:ss:lodging:lodginginterface:messages:availdetailtypes:defn:v3")
(xml/declare-ns "xml15" "urn:expedia:e3:data:revitemtypes:defn:v1")
(xml/declare-ns "xml16" "urn:expedia:e3:data:financetypes:defn:v4")
(xml/declare-ns "xml17" "urn:expedia:e3:data:airtypes:defn:v6")
(xml/declare-ns "xml18" "urn:expedia:e3:data:ancillarytypes:defn:v2")
(xml/declare-ns "xml19" "urn:expedia:e3:data:naturalkeytypes:defn:v1")
(xml/declare-ns "xml20" "urn:expedia:e3:ews:core:trip:messages:defn:v1")
(xml/declare-ns "xml21" "urn:expedia:e3:data:naturalproducttypes:defn:v1")
(xml/declare-ns "xml22" "urn:expedia:e3:data:traveltypes:defn:v4")
(xml/declare-ns "xml23" "urn:expedia:e3:insurance:basetypes:v1")
(xml/declare-ns "xml24" "urn:expedia:e3:insurance:basetypes:v2")
(xml/declare-ns "xml25" "urn:expedia:e3:shop:packages:messages:ordertypes:defn:v1")
(xml/declare-ns "xml26" "urn:expedia:e3:shop:packages:messages:common:defn:v3")
(xml/declare-ns "xml27" "urn:expedia:e3:ews:core:itinerary:producttypes:defn:v2")
(xml/declare-ns "xml28" "urn:expedia:e3:data:cartypes:defn:v5")
(xml/declare-ns "xml29" "urn:expedia:e3:data:cruise:types:v1")
(xml/declare-ns "xml30" "urn:expedia:s3:edx:data:edxtypes:defn:v1")
(xml/declare-ns "xml31" "urn:expedia:om:common:defn:v1")
(xml/declare-ns "xml32" "urn:expedia:e3:data:errortypes:defn:v4")
(xml/declare-ns "xml33" "urn:expedia:payment:processor:messages:v2")
(xml/declare-ns "xml34" "urn:expedia:e3:data:revenueoptimizationtypes:defn:v1")
(xml/declare-ns "xml35" "urn:expedia:om:datatype:defn:v1")
(xml/declare-ns "xml36" "urn:com:expedia:s3:cars:data:carerrortypes:defn:v2")
(xml/declare-ns "xml37" "urn:expedia:payment:messages:v1")
(xml/declare-ns "xml38" "urn:expedia:e3:ss:lodging:lodginginterface:messages:preparepurchasetypes:defn:v2")
(xml/declare-ns "xml39" "urn:expedia:e3:ss:lodging:lodginginterface:messages:purchasetypes:defn:v2")
(xml/declare-ns "xml40" "urn:expedia:payment:system:types:v2")
(xml/declare-ns "xml41" "urn:expedia:om:supply:messages:defn:v1")
(xml/declare-ns "xml42" "urn:expedia:om:supply:datatype:defn:v1")
(xml/declare-ns "xml43" "urn:expedia:e3:cpn:domain:basetypes:defn:v2")
(xml/declare-ns "xml44" "urn:expedia:e3:partnerlty:sterling:types:v1")
(xml/declare-ns "xml45" "urn:expedia:e3:ss:lodging:lodginginterface:changecommontypes:defn:v1")
(xml/declare-ns "xml46" "urn:expedia:e3:ss:lodging:lodginginterface:messages:softchangetypes:defn:v1")
(xml/declare-ns "xml47" "urn:expedia:payment:omsadapter:types:v1")
(xml/declare-ns "xml48" "urn:expedia:e3:lty:domain:pointbanktypes:defn:v1")
(xml/declare-ns "xml49" "urn:expedia:e3:lty:domain:adaptorproxy:defn:v1")
(xml/declare-ns "xml50" "urn:expedia:e3:lty:domain:commontypes:defn:v1")
(xml/declare-ns "xml51" "urn:expedia:e3:lty:domain:offertypes:defn:v1")
(xml/declare-ns "xml52" "urn:expedia:e3:lty:domain:basetypes:defn:v1")
(xml/declare-ns "xml53" "urn:expedia:e3:cpn:domain:activitytypes:defn:v1")
(xml/declare-ns "xml54" "urn:expedia:e3:cpn:domain:contentprovidertypes:defn:v1")
(xml/declare-ns "xml55" "urn:expedia:e3:shop:packages:messages:changetypes:defn:v1")
(xml/declare-ns "xml56" "urn:expedia:e3:shop:presto:datatype:v1")
(xml/declare-ns "xml57" "urn:expedia:e3:data:messagetypes:defn:v4")
(xml/declare-ns "xml58" "urn:expedia:e3:ews:core:messages:common:defn:v1")