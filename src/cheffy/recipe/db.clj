(ns cheffy.recipe.db
  (:require [cheffy.in :as in]
            [cheffy.out :as out]
            [cheffy.types :as types]
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
  [db :- types/Database, recipe :- (assoc in/RecipeCreate :recipe-id s/Str :uid s/Str :public s/Bool)]
  (sql/insert! db :recipe (assoc recipe :public false)))

(s/defn update-recipe! :- s/Bool
  [db :- types/Database, recipe :- (assoc in/RecipeUpdate :recipe-id s/Str)]
  (let [update (sql/update! db :recipe (dissoc recipe :recipe-id) (select-keys recipe [:recipe-id]))]
    (= (:next.jdbc/update-count update) 1)))

(s/defn delete! :- s/Bool
  [db :- types/Database, recipe-id :- s/Str]
  (let [update (sql/delete! db :recipe {:recipe-id recipe-id})]
    (= (:next.jdbc/update-count update) 1)))
