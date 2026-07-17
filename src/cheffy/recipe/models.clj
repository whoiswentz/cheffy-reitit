(ns cheffy.recipe.models
  (:require [cheffy.recipe.in :as in]
            [schema.core :as s]))

(s/defschema NewRecipe
  (merge in/RecipeCreate
         {:recipe-id s/Str
          :uid s/Str
          :public s/Bool}))

(s/defschema NewStep
  (merge in/StepCreate
         {:recipe-id s/Str
          :step-id s/Str}))

(s/defschema RecipeChanges
  (merge in/RecipeUpdate
         {:recipe-id s/Str}))

(s/defschema NewIngredient
  (merge in/IngredientCreate
         {:recipe-id s/Str
          :ingredient-id s/Str}))

(s/defschema IngredientChanges
  in/IngredientUpdate)
