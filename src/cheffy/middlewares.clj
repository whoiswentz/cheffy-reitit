(ns cheffy.middlewares
  (:require [cheffy.recipe.application.queries :as queries]
            [cheffy.types :as types]
            [ring.middleware.jwt :as jwt]
            [ring.util.response :as rr]
            [schema.core :as s]))

(s/def wrap-auth0 :- types/AuthMiddleware
  {:name ::auth0
   :description "Middleware for auth0"
   :wrap (fn [handler]
           (jwt/wrap-jwt
            handler
            {:issuers {"https://dev-l6x6wetr1ruqvu3s.us.auth0.com/"
                       {:alg :RS256
                        :jwk-endpoint "https://dev-l6x6wetr1ruqvu3s.us.auth0.com/.well-known/jwks.json"}}}))})

(s/def wrap-recipe-owner :- types/OwnerMiddleware
  {:name        ::recipe-owner
   :description "Middleware to check if a requestor is a recipe owner"
   :wrap        (fn [handler db]
                  (fn [request]
                    (let [uid (-> request :claims :sub)
                          recipe-id (-> request :parameters :path :recipe-id)
                          recipe (queries/get-recipe db recipe-id)]
                      (if (= (:recipe/uid recipe) uid)
                        (handler request)
                        (-> (rr/response {:message "You need to be the recipe owner"
                                          :data    (str "recipe-id " recipe-id)
                                          :type    :authorization-required})
                            (rr/status 401))))))})
