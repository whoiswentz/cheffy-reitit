(defproject cheffy-reitit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.13.0"]
                 [integrant "0.13.1"]
                 [environ "1.2.0"]
                 [metosin/reitit "0.8.0-alpha1"]
                 [com.github.seancorfield/next.jdbc "1.3.994"]
                 [org.postgresql/postgresql "42.2.10"]
                 [clj-http "3.13.0"]
                 [ovotech/ring-jwt "2.3.0"]]
  :profiles {:uberjar {:aot :all}
             :dev {:source-paths ["dev/src"]
                   :resource-paths ["dev/resources"]
                   :dependencies [[ring/ring-mock "0.4.0"]
                                  [integrant/repl "0.4.0"]]}}
  :uberjar-name "cheffy.jar")
