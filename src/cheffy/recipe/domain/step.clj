(ns cheffy.recipe.domain.step
  (:require [cheffy.recipe.api.in :as in]
            [schema.core :as s]))

(s/defschema Step
  {:step-id     s/Str
   :recipe-id   s/Str
   :description s/Str
   :sort        s/Int})

(s/defn new-step :- Step
  [command :- in/StepCreate, recipe-id :- s/Str]
  (assoc command
         :step-id (str (java.util.UUID/randomUUID))
         :recipe-id recipe-id))
