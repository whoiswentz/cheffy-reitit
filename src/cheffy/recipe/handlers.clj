(ns cheffy.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [ring.util.response :as rr]
            [cheffy.responses :as responses])
  (:import (java.util UUID)))

(defn list-all-recipes
  [db]
  (fn [request]
    (let [uid "auth0|5ef440986e8fbb001355fd9c"
          recipes (recipe-db/find-all db uid)]
      (rr/response recipes))))

(defn retrieve-recipe
  [db]
  (fn [request]
    (let [recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"]
      (if-let [recipe (recipe-db/find-by-id db recipe-id)]
        (rr/response recipe)
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))

(defn create-recipe!
  [db]
  (fn [request]
    (let [recipe-id (str (UUID/randomUUID))
          uid       "auth0|5ef440986e8fbb001355fd9c"
          recipe    (assoc (-> request :parameters :body)
                           :recipe-id recipe-id
                           :uid uid)]
      (recipe-db/insert! db recipe)
      (rr/created (str responses/base-url "/recipes/" recipe-id) {:recipe-id recipe-id}))))

(defn update-recipe!
  [db]
  (fn [request]
    (let [recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"
          recipe    (assoc (-> request :parameters :body) :recipe-id recipe-id)]
      (if (recipe-db/update-recipe! db recipe)
        {:status 204}
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))

(defn delete-recipe!
  [db]
  (fn [request]
    (let [recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"]
      (if (recipe-db/delete! db recipe-id)
        {:status 204}
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id-" recipe-id)})))))
