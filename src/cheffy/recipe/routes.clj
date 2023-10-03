(ns cheffy.recipe.routes
  (:require [cheffy.recipe.handlers :as recipe]
            [cheffy.responses :as responses]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/recipes" {:swagger {:tags ["recipes"]}}
     [""
      {:get {:handler   (recipe/list-all-recipes db)
             :summary   "list all recipes"
             :responses {200 {:body responses/recipes}}}}]
     ["/:recipe-id"
      {:get {:handler    (recipe/retrieve-recipe db)
             :parameters {:path {:recipe-id string?}}
             :summary    "list by id"
             :responses  {200 {:body responses/recipe}}}}]]))