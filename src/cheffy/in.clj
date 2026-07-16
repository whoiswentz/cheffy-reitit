(ns cheffy.in
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
