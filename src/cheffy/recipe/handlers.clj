(ns cheffy.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [ring.util.response :as rr]))

(defn list-all-recipes
  [db]
  (fn [request]
    (let [recipes (recipe-db/find-all-recipes db "")]
      (rr/response recipes))))

(defn retrieve-recipe
  [db]
  (fn [request]
    (let [recipe (recipe-db/find-recipe-by-id db "")]
      (if recipe
        (rr/response recipe)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    {:recipe-id ""}})))))