(ns cheffy.recipe.in
  (:require [schema.core :as s]))

(s/defschema RecipeCreate
  {:name s/Str
   :prep-time s/Num
   :img s/Str})

(s/defschema RecipeUpdate
  {:name s/Str
   :prep-time s/Int
   :public s/Bool
   :img s/Str})

(s/defschema StepCreate
  {:description s/Str
   :sort s/Int})

(s/defschema StepUpdate
  {:step-id s/Str
   :description s/Str
   :sort s/Int})

(s/defschema IngredientCreate
  {:name s/Str
   :amount s/Int
   :measure s/Str
   :sort s/Int})

(s/defschema IngredientUpdate
  {:ingredient-id s/Str
   :name s/Str
   :amount s/Int
   :measure s/Str
   :sort s/Int})
