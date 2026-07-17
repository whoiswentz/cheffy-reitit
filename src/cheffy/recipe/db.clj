(ns cheffy.recipe.db
  (:require [cheffy.recipe.in :as in]
            [cheffy.recipe.models :as models]
            [cheffy.recipe.out :as out]
            [cheffy.types :as types]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [schema.core :as s])
  (:import (clojure.lang IPersistentMap)))

(s/defn find-all :- out/Recipes
  [db :- types/Database, uid :- (s/maybe s/Str)]
  (cond-> {:public (sql/find-by-keys db :recipe {:public true})}
    uid (assoc :drafts (sql/find-by-keys db :recipe {:public false :uid uid}))))

(s/defn find-by-id :- (s/maybe out/Recipe)
  [db :- types/Database, recipe-id :- s/Str]
  (when-let [recipe (first (sql/find-by-keys db :recipe {:recipe-id recipe-id}))]
    (assoc recipe
           :recipe/steps (sql/find-by-keys db :step {:recipe-id recipe-id})
           :recipe/ingredients (sql/find-by-keys db :ingredient {:recipe-id recipe-id}))))

(s/defn insert! :- IPersistentMap
  [db :- types/Database, recipe :- models/NewRecipe]
  (sql/insert! db :recipe (assoc recipe :public false)))

(s/defn insert-step! :- IPersistentMap
  [db :- types/Database, step :- models/NewStep]
  (sql/insert! db :step step))

(s/defn update-recipe! :- s/Bool
  [db :- types/Database, recipe :- models/RecipeChanges]
  (let [update (sql/update! db :recipe (dissoc recipe :recipe-id) (select-keys recipe [:recipe-id]))]
    (= (:next.jdbc/update-count update) 1)))

(s/defn update-step! :- s/Bool
  [db :- types/Database, step :- in/StepUpdate]
  (let [update (sql/update! db :step (dissoc step :step-id) (select-keys step [:step-id]))]
    (= (:next.jdbc/update-count update) 1)))

(s/defn delete! :- s/Bool
  [db :- types/Database, recipe-id :- s/Str]
  (let [update (sql/delete! db :recipe {:recipe-id recipe-id})]
    (= (:next.jdbc/update-count update) 1)))

(s/defn delete-step! :- s/Bool
  [db :- types/Database, step-id :- s/Str]
  (let [update (sql/delete! db :step {:step-id step-id})]
    (= (:next.jdbc/update-count update) 1)))

(s/defn insert-ingredient! :- IPersistentMap
  [db :- types/Database, ingredient :- models/NewIngredient]
  (sql/insert! db :ingredient ingredient))

(s/defn update-ingredient! :- s/Bool
  [db :- types/Database, ingredient :- in/IngredientUpdate]
  (let [update (sql/update! db :ingredient (dissoc ingredient :ingredient-id) (select-keys ingredient [:ingredient-id]))]
    (= (:next.jdbc/update-count update) 1)))

(s/defn delete-ingredient! :- s/Bool
  [db :- types/Database, ingredient-id :- s/Str]
  (let [update (sql/delete! db :ingredient {:ingredient-id ingredient-id})]
    (= (:next.jdbc/update-count update) 1)))

(s/defn favorite-recipe! :- IPersistentMap
  [db :- types/Database
   {:keys [recipe-id] :as data}]
  (jdbc/with-transaction [tx db]
    (sql/insert! tx :recipe-favorite data (:options db))
    (jdbc/execute-one! tx ["UPDATE recipe
                            SET favorite_count = favorite_count + 1
                            WHERE recipe_id = ?" recipe-id])))

(s/defn unfavorite-recipe! :- s/Bool
  [db :- types/Database
   {:keys [recipe-id] :as data}]
  (-> (jdbc/with-transaction [tx db]
        (sql/delete! tx :recipe-favorite data (:options db))
        (jdbc/execute-one! tx ["UPDATE recipe
                            SET favorite_count = favorite_count - 1
                            WHERE recipe_id = ?" recipe-id]))
      :next.jdbc/update-count
      (pos?)))
