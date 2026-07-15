(ns cheffy.middlewares
  (:require [ring.middleware.jwt :as jwt]))

(def wrap-auth0
  {:name ::auth0
   :description "Middleware for auth0"
   :wrap (fn [handler]
           (jwt/wrap-jwt
             handler
             {:alg :RS256
              :jwk-endpoint "https://dev-l6x6wetr1ruqvu3s.us.auth0.com/.well-known/jwks.json"}))})
