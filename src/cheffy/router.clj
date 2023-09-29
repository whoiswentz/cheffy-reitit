(ns cheffy.router
  (:require [cheffy.recipe.routes :as recipe]
            [cheffy.swagger :as swagger]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]))

(def router-config
  {:data {:muuntaja   m/instance
          :middleware [(:middleware swagger/handler)
                       muuntaja/format-middleware]}})

(defn routes
  [env]
  (ring/ring-handler
    (ring/router
      [(:config swagger/handler)
       ["/v1"
        (recipe/routes env)]]
      router-config)
    (ring/routes
      (:swagger-ui swagger/handler))))