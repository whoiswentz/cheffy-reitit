(ns cheffy.middlewares
  (:require [cheffy.recipe.db :as recipe-db]
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
                          recipe (recipe-db/find-by-id db recipe-id)]
                      (if (= (:recipe/uid recipe) uid)
                        (handler request)
                        (-> (rr/response {:message "You need to be the recipe owner"
                                          :data    (str "recipe-id " recipe-id)
                                          :type    :authorization-required})
                            (rr/status 401))))))})

(def wrap-manage-recipes
  {:name        ::manage-recipes
   :description "Middleware to check if a user can manage recipes"
   :wrap        (fn [handler]
                  (fn [request]
                    (let [roles (get-in request [:claims "https://api.alchemists.stream/roles"])]
                      (if (some #{"manage-roles"} roles)
                        (handler request)
                        (-> (rr/response {:message "You need to be a cook to manager recipes"
                                          :data    (:uri request)
                                          :type    :authorization-required})
                            (rr/status 401))))))})
