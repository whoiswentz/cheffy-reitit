(ns cheffy.server
  (:require [cheffy.router :as router]
            [cheffy.types :as types]
            [clojure.tools.logging :as log]
            [environ.core :refer [env]]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [ring.adapter.jetty :as jetty]
            [schema.core :as s])
  (:import (clojure.lang IPersistentMap)
           (org.eclipse.jetty.server Server)))

(s/defn app :- types/Handler
  [env :- types/Env]
  (router/routes env))

(s/defmethod ig/expand-key :server/jetty :- IPersistentMap
  [key :- s/Keyword, config :- IPersistentMap]
  {key (if-let [port (env :port)]
         (assoc config :port (Integer/parseInt port))
         config)})

(s/defmethod ig/init-key :server/jetty :- Server
  [_ :- s/Keyword
   {:keys [handler port]} :- {:handler types/Handler :port s/Int s/Keyword Object}]
  (log/info "Server running on port" port)
  (jetty/run-jetty handler {:port  port
                            :join? false}))

(s/defmethod ig/init-key :cheffy/app :- types/Handler
  [_ :- s/Keyword, config :- types/Env]
  (log/info "App started")
  (app config))

(s/defmethod ig/expand-key :db/postgres :- IPersistentMap
  [key :- s/Keyword, config :- IPersistentMap]
  {key (if-let [jdbc-url (env :jdbc-url)]
         (assoc config :jdbc-url jdbc-url)
         config)})

(s/defmethod ig/init-key :db/postgres :- types/Database
  [key :- s/Keyword, {:keys [jdbc-url]} :- {:jdbc-url s/Str s/Keyword Object}]
  (log/info "Configured db" key)
  (jdbc/with-options jdbc-url jdbc/snake-kebab-opts))

(s/defmethod ig/expand-key :auth/auth0 :- IPersistentMap
  [key :- s/Keyword, config :- IPersistentMap]
  {key (cond-> config
         (env :auth0-domain)        (assoc :domain (env :auth0-domain))
         (env :auth0-client-id)     (assoc :client-id (env :auth0-client-id))
         (env :auth0-client-secret) (assoc :client-secret (env :auth0-client-secret)))})

(s/defmethod ig/init-key :auth/auth0 :- types/Auth0
  [_ :- s/Keyword, config :- types/Auth0]
  config)

(s/defmethod ig/halt-key! :server/jetty :- (s/maybe Object)
  [_ :- s/Keyword, jetty :- Server]
  (.stop jetty))

(s/defn -main :- IPersistentMap
  [config-file :- s/Str]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/expand ig/init)))
