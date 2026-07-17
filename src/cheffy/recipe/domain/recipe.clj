(ns cheffy.recipe.domain.recipe
  (:require [cheffy.recipe.api.in :as in]
            [schema.core :as s]))

(s/defschema Recipe
  {:recipe-id s/Str
   :uid       s/Str
   :name      s/Str
   :prep-time s/Num
   :img       s/Str
   :public    s/Bool})

(s/defschema RecipeChanges
  {:recipe-id s/Str
   :name      s/Str
   :prep-time s/Int
   :public    s/Bool
   :img       s/Str})

(s/defn new-recipe :- Recipe
  [command :- in/RecipeCreate, uid :- s/Str]
  (assoc command
         :recipe-id (str (java.util.UUID/randomUUID))
         :uid uid
         :public false))
