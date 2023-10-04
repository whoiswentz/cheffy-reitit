(defproject cheffy "0.1.0-SNAPSHOT"
  :description "Cheffy REST API"
  :url "https://api.learnreitit.com"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring "1.10.0"]
                 [integrant "0.8.1"]
                 [environ "1.2.0"]
                 [fi.metosin/reitit "0.7.0-alpha6"]
                 [com.github.seancorfield/next.jdbc "1.3.883"]
                 [org.postgresql/postgresql "42.6.0"]
                 [clj-http "3.12.3"]
                 [net.clojars.kelveden/ring-jwt "2.4.0"]
                 [camel-snake-kebab "0.4.3"]]
  :main ^:skip-aot cheffy.server
  :target-path "target/%s"
  :uberjar-name "cheffy.jar"
  :profiles {:uberjar {:aot      :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :dev     {:source-paths    ["dev/src"]
                       :resources-paths ["dev/resources"]
                       :dependencies    [[ring/ring-mock "0.4.0"]
                                         [integrant/repl "0.3.3"]]}})
