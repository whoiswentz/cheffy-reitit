(ns cheffy.recipe.application.commands
  (:require [cheffy.recipe.api.in :as in]
            [cheffy.recipe.domain.recipe :as recipe-domain]
            [cheffy.recipe.domain.step :as step-domain]
            [cheffy.recipe.infrastructure.repository :as repo]
            [cheffy.types :as types]
            [schema.core :as s]))

(s/defn create-recipe! :- {:recipe-id s/Str}
  [db :- types/Database, command :- in/RecipeCreate, uid :- s/Str]
  (let [recipe (recipe-domain/new-recipe command uid)]
    (repo/save-recipe! db recipe)
    {:recipe-id (:recipe-id recipe)}))

(s/defn update-recipe! :- s/Bool
  [db :- types/Database, recipe-id :- s/Str, changes :- in/RecipeUpdate]
  (repo/update-recipe! db (assoc changes :recipe-id recipe-id)))

(s/defn delete-recipe! :- s/Bool
  [db :- types/Database, recipe-id :- s/Str]
  (repo/delete-recipe! db recipe-id))

(s/defn favorite-recipe! :- s/Bool
  [db :- types/Database, recipe-id :- s/Str, uid :- s/Str]
  (repo/favorite-recipe! db {:recipe-id recipe-id :uid uid})
  true)

(s/defn unfavorite-recipe! :- s/Bool
  [db :- types/Database, recipe-id :- s/Str, uid :- s/Str]
  (repo/unfavorite-recipe! db {:recipe-id recipe-id :uid uid}))

(s/defn create-step! :- {:step-id s/Str}
  [db :- types/Database, command :- in/StepCreate, recipe-id :- s/Str]
  (let [step (step-domain/new-step command recipe-id)]
    (repo/save-step! db step)
    {:step-id (:step-id step)}))

(s/defn update-step! :- s/Bool
  [db :- types/Database, changes :- in/StepUpdate]
  (repo/update-step! db changes))

(s/defn delete-step! :- s/Bool
  [db :- types/Database, step-id :- s/Str]
  (repo/delete-step! db step-id))
