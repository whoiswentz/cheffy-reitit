(ns cheffy.recipe.application.queries
  (:require [cheffy.recipe.api.out :as out]
            [cheffy.recipe.infrastructure.repository :as repo]
            [cheffy.types :as types]
            [schema.core :as s]))

(s/defn list-recipes :- out/Recipes
  [db :- types/Database, uid :- (s/maybe s/Str)]
  (repo/find-all db uid))

(s/defn get-recipe :- (s/maybe out/Recipe)
  [db :- types/Database, recipe-id :- s/Str]
  (repo/find-by-id db recipe-id))
