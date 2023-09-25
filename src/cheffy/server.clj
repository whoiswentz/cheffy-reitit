(ns cheffy.server
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]
            [environ.core :refer [env]]))

(defn app
  [env]
  (ring/ring-handler
   (ring/router [["/" {:get {:handler (fn [req] {:status 200
                                                 :body "Hello World"})}}]])))
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

(defmethod ig/prep-key :server/jetty
  [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defn -main
  [config-file]
  (-> config-file
      slurp
      ig/read-string
      ig/init))
