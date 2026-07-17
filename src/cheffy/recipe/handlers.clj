(ns cheffy.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [cheffy.types :as types]
            [ring.util.response :as rr]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn list-all-recipes :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [uid (-> request :claims :sub)
          recipes (recipe-db/find-all db uid)]
      (rr/response recipes))))

(s/defn retrieve-recipe :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)]
      (if-let [recipe (recipe-db/find-by-id db recipe-id)]
        (rr/response recipe)
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))

(s/defn create-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [recipe-id (str (UUID/randomUUID))
          uid       (-> request :claims :sub)
          recipe    (assoc (-> request :parameters :body)
                           :recipe-id recipe-id
                           :uid uid)]
      (recipe-db/insert! db recipe)
      (rr/created (str "localhost/recipes/" recipe-id) {:recipe-id recipe-id}))))

(s/defn update-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          recipe    (assoc (-> request :parameters :body) :recipe-id recipe-id)]
      (if (recipe-db/update-recipe! db recipe)
        (rr/status 204)
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))

(s/defn delete-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)]
      (if (recipe-db/delete! db recipe-id)
        (rr/status 204)
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))

(s/defn favorite-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [uid (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)]
      (recipe-db/favorite-recipe! db {:uid uid :recipe-id recipe-id})
      (rr/status 204))))

(s/defn unfavorite-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [uid (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)
          deleted? (recipe-db/unfavorite-recipe! db {:uid uid :recipe-id recipe-id})]
      (if deleted?
        (rr/status 204)
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))

(s/defn create-step! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [step-id   (str (UUID/randomUUID))
          recipe-id (-> request :parameters :path :recipe-id)
          step      (assoc (-> request :parameters :body)
                           :recipe-id recipe-id
                           :step-id step-id)]
      (recipe-db/insert-step! db step)
      (rr/created (str "localhost/recipes/" recipe-id "/steps/" step-id) {:step-id step-id}))))

(s/defn update-step! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [step (-> request :parameters :body)]
      (if (recipe-db/update-step! db step)
        (rr/status 204)
        (rr/not-found {:type "step-not-found"
                       :message "Step not found"})))))

(s/defn delete-step! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [step-id (-> request :parameters :body :step-id)]
      (if (recipe-db/delete-step! db step-id)
        (rr/status 204)
        (rr/not-found {:type "step-not-found"
                       :message "Step not found"})))))

(s/defn create-ingredient! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [ingredient-id (str (UUID/randomUUID))
          recipe-id     (-> request :parameters :path :recipe-id)
          ingredient    (assoc (-> request :parameters :body)
                               :recipe-id recipe-id
                               :ingredient-id ingredient-id)]
      (recipe-db/insert-ingredient! db ingredient)
      (rr/created (str "localhost/recipes/" recipe-id "/ingredients/" ingredient-id)
                  {:ingredient-id ingredient-id}))))

(s/defn update-ingredient! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [ingredient (-> request :parameters :body)]
      (if (recipe-db/update-ingredient! db ingredient)
        (rr/status 204)
        (rr/not-found {:type "ingredient-not-found"
                       :message "Ingredient not found"})))))

(s/defn delete-ingredient! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [ingredient-id (-> request :parameters :body :ingredient-id)]
      (if (recipe-db/delete-ingredient! db ingredient-id)
        (rr/status 204)
        (rr/not-found {:type "ingredient-not-found"
                       :message "Ingredient not found"})))))
