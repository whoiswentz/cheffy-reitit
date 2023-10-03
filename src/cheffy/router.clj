(ns cheffy.router
  (:require [cheffy.recipe.routes :as recipe]
            [cheffy.swagger :as swagger]
            [muuntaja.core :as m]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]))

(def router-config
  {:data {:coercion   coercion-spec/coercion
          :muuntaja   m/instance
          :middleware [(:middleware swagger/handler)
                       muuntaja/format-middleware
                       exception/exception-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-response-middleware]}})

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