(ns cheffy.router
  (:require [cheffy.recipe.routes :as recipe]
            [cheffy.types :as types]
            [muuntaja.core :as m]
            [reitit.coercion.schema :as coercion-schema]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.spec :as rs]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [schema.core :as s]))

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
   :data {:coercion   coercion-schema/coercion
          :muuntaja   m/instance
          :middleware [swagger/swagger-feature
                       muuntaja/format-middleware
                       exception/exception-middleware
                       coercion/coerce-request-middleware]}})

(s/defn routes :- types/Handler
  [env :- types/Env]
  (ring/ring-handler
   (ring/router [swagger-docs ["/v1" (recipe/routes env)]] router-config)
   (ring/routes (swagger-ui/create-swagger-ui-handler {:path "/"}))))