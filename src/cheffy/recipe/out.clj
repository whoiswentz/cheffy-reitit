(ns cheffy.recipe.out
  (:require [schema.core :as s]))

(s/defschema Step
  {:step/step-id s/Str
   :step/sort s/Int
   :step/description s/Str
   :step/recipe-id s/Str})

(s/defschema Ingredient
  {:ingredient/ingredient-id s/Str
   :ingredient/sort s/Int
   :ingredient/name s/Str
   :ingredient/amount s/Int
   :ingredient/measure s/Str
   :ingredient/recipe-id s/Str})

(s/defschema Recipe
  {:recipe/public s/Bool
   (s/optional-key :recipe/favorite-count) s/Int
   :recipe/recipe-id s/Str
   :recipe/name s/Str
   :recipe/uid s/Str
   :recipe/prep-time s/Num
   :recipe/img s/Str
   (s/optional-key :recipe/steps) [Step]
   (s/optional-key :recipe/ingredients) [Ingredient]})

(s/defschema Recipes
  {:public [Recipe]
   (s/optional-key :drafts) [Recipe]})
