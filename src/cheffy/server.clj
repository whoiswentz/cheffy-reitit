(ns cheffy.server
  (:require [cheffy.router :as router]
            [environ.core :refer [env]]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/init-key :server/jetty
  [_ {:keys [handler, port]}]
  (jetty/run-jetty handler {:port port :join? false})
  (println (str "\nServer running on port " port)))

(defmethod ig/halt-key! :server/jetty
  [_ jetty]
  (.stop jetty))

(defmethod ig/init-key :cheffy/app
  [_ config]
  (app config))

(defmethod ig/init-key :db/postgres
  [_ config]
  (:jdbc-url config))

(defmethod ig/prep-key :db/postgres
  [_ config]
  (merge config {:jdbc-url (env :jdbc-url)}))

(defmethod ig/prep-key :server/jetty
  [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defn -main
  [config-file]
  (-> config-file
      slurp
      ig/read-string
      ig/init))
