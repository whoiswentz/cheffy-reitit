(ns cheffy.server
  (:require [cheffy.router :as router]
            [environ.core :refer [env]]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
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
  [_ {:keys [jdbc-url]}]
  (println jdbc-url)
  (jdbc/with-options jdbc-url jdbc/snake-kebab-opts))

(defmethod ig/prep-key :db/postgres
  [_ config]
  (println config)
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
