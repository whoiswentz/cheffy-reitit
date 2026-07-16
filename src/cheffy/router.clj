(ns cheffy.router
  (:require [cheffy.recipe.routes :as recipe]
            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.spec :as rs]))

(def swagger-docs
  ["/swagger.json" {:get {:no-doc  true
                          :swagger {:basePath "/"
                                    :info     {:title       "Cheffy API reference"
                                               :description "Cheffy API"
                                               :version     "1.0.0"}}
                          :handler (swagger/create-swagger-handler)}}])

(def router-config
  {:validate rs/validate
   :exception pretty/exception
   :data {:coercion coercion-spec/coercion
          :muuntaja   m/instance
          :middleware [swagger/swagger-feature
                       muuntaja/format-middleware
                       exception/exception-middleware
                       coercion/coerce-request-middleware]}})

(defn routes
  [env]
  (ring/ring-handler
    (ring/router [swagger-docs ["/v1" (recipe/routes env)]] router-config)
    (ring/routes (swagger-ui/create-swagger-ui-handler {:path "/"}))))