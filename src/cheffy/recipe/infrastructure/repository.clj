(ns cheffy.recipe.infrastructure.repository
  (:require [cheffy.recipe.api.in :as in]
            [cheffy.recipe.api.out :as out]
            [cheffy.recipe.domain.recipe :as recipe-domain]
            [cheffy.recipe.domain.step :as step-domain]
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

(s/defn save-recipe! :- IPersistentMap
  [db :- types/Database, recipe :- recipe-domain/Recipe]
  (sql/insert! db :recipe recipe))

(s/defn update-recipe! :- s/Bool
  [db :- types/Database, recipe :- recipe-domain/RecipeChanges]
  (let [result (sql/update! db :recipe (dissoc recipe :recipe-id) (select-keys recipe [:recipe-id]))]
    (= (:next.jdbc/update-count result) 1)))

(s/defn delete-recipe! :- s/Bool
  [db :- types/Database, recipe-id :- s/Str]
  (let [result (sql/delete! db :recipe {:recipe-id recipe-id})]
    (= (:next.jdbc/update-count result) 1)))

(s/defn save-step! :- IPersistentMap
  [db :- types/Database, step :- step-domain/Step]
  (sql/insert! db :step step))

(s/defn update-step! :- s/Bool
  [db :- types/Database, step :- in/StepUpdate]
  (let [result (sql/update! db :step (dissoc step :step-id) (select-keys step [:step-id]))]
    (= (:next.jdbc/update-count result) 1)))

(s/defn delete-step! :- s/Bool
  [db :- types/Database, step-id :- s/Str]
  (let [result (sql/delete! db :step {:step-id step-id})]
    (= (:next.jdbc/update-count result) 1)))

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
