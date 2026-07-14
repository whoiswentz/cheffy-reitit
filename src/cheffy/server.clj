(ns cheffy.server
  (:require [cheffy.router :as router]
            [environ.core :refer [env]]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/expand-key :server/jetty
  [key config]
  {key (if-let [port (env :port)]
       (assoc config :port (Integer/parseInt port))
       config)})

(defmethod ig/init-key :server/jetty
  [_ {:keys [handler port]}]
  (println (str "\nServer running on port " port))
  (jetty/run-jetty handler {:port  port
                            :join? false}))

(defmethod ig/init-key :cheffy/app
  [_ config]
  (println "\nApp started")
  (app config))

(defmethod ig/expand-key :db/postgres
  [key config]
  {key (if-let [jdbc-url (env :jdbc-url)]
       (assoc config :jdbc-url jdbc-url)
       config)})

(defmethod ig/init-key :db/postgres
  [_ config]
  (println "\nConfigured db")
  (:jdbc-url config))

(defmethod ig/halt-key! :server/jetty
  [_ jetty]
  (.stop jetty))

(defn -main
  [config-file]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/expand ig/init)))
