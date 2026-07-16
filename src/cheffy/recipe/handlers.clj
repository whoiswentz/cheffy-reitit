(ns cheffy.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [cheffy.types :as types]
            [ring.util.response :as rr]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn list-all-recipes :- types/Handler
  [db :- types/Database]
  (s/fn [request :- types/RingRequest]
    (let [uid (-> request :claims :sub)
          recipes (recipe-db/find-all db uid)]
      (rr/response recipes))))

(s/defn retrieve-recipe :- types/Handler
  [db :- types/Database]
  (s/fn [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)]
      (if-let [recipe (recipe-db/find-by-id db recipe-id)]
        (rr/response recipe)
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))

(s/defn create-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn [request :- types/RingRequest]
    (let [recipe-id (str (UUID/randomUUID))
          uid       (-> request :claims :sub)
          recipe    (assoc (-> request :parameters :body)
                           :recipe-id recipe-id
                           :uid uid)]
      (recipe-db/insert! db recipe)
      (rr/created (str "localhost/recipes/" recipe-id) {:recipe-id recipe-id}))))

(s/defn update-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          recipe    (assoc (-> request :parameters :body) :recipe-id recipe-id)]
      (if (recipe-db/update-recipe! db recipe)
        {:status 204}
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))

(s/defn delete-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)]
      (if (recipe-db/delete! db recipe-id)
        {:status 204}
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))
