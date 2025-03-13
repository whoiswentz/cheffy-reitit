(ns cheffy-reitit.server
  (:require [environ.core :refer [env]]
            [integrant.core :as ig]
            [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [cheffy-reitit.router :as router]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/prep-key :server/jetty
  [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/init-key :server/jetty
  [_, {:keys [handler, port]}]
  (jetty/run-jetty handler {:port port :join? false})
  (println (str "\n Server running on port " port)))

(defmethod ig/halt-key! :server/jetty
  [_ jetty]
  (.interrupt jetty))

(defmethod ig/init-key :cheffy/app
  [_ config]
  (app config))

(defmethod ig/init-key :db/postgres
  [_ config]
  (:jdbc-url config))

(defn -main
  [config-file]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/prep ig/init)))
