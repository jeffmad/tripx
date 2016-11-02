(defproject tripx "0.1.0-SNAPSHOT"
  :description "a service that reads trip xml and converts to json"
  :url "http://ewegithub.sb.karmalab.net/EWE/tripx"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0-RC4"]
                 [com.stuartsierra/component "0.3.1"]
                 [compojure "1.4.0"]
                 [duct "0.5.6"]
                 [environ "1.0.1"]
                 [meta-merge "0.1.1"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-jetty-component "0.3.0"]
                 [clj-time "0.11.0"]
                 [clj-http "2.0.0"]
                 [org.slf4j/log4j-over-slf4j "1.7.13"]
                 [org.slf4j/jcl-over-slf4j "1.7.13"]
                 [org.slf4j/jul-to-slf4j "1.7.13"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/data.zip "0.1.1"]
                 [org.clojure/data.xml "0.1.0-beta1"]]
  :plugins [[lein-environ "1.0.1"]
            [lein-gen "0.2.2"]]
  :generators [[duct/generators "0.5.6"][lein-gen/generators "0.2.2"]]
  :duct {:ns-prefix tripx}
  :main ^:skip-aot tripx.main
  :target-path "target/%s/"
  :aliases {"gen"   ["generate"]
            "setup" ["do" ["generate" "locals"]]}
  :profiles
  {:dev  [:project/dev  :profiles/dev]
   :test [:project/test :profiles/test]
   :uberjar {:aot :all}
   :profiles/dev  {}
   :profiles/test {}
   :project/dev   {:dependencies [[reloaded.repl "0.2.1"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [eftest "0.1.0"]
                                  [kerodon "0.7.0"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}
                   :env {:port 3000}}
   :project/test  {}})
