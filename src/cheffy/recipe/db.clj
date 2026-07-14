(ns cheffy.recipe.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn find-all-recipes
  [db uid]
  (with-open [conn (jdbc/get-connection db)]
    (cond-> {:public (sql/find-by-keys conn :recipe {:public true})}
      uid (assoc :drafts (sql/find-by-keys conn :recipe {:public false :uid uid})))))

(defn find-recipe-by-id
  [db recipe-id]
  (with-open [conn (jdbc/get-connection db)]
    (when-let [recipe (first (sql/find-by-keys conn :recipe {:recipe_id recipe-id}))]
      (assoc recipe
        :recipe/steps (sql/find-by-keys conn :step {:recipe_id recipe-id})
        :recipe/ingredients (sql/find-by-keys conn :ingredient {:recipe_id recipe-id})))))
