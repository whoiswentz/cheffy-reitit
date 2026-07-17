(ns cheffy.recipe.api.routes
  (:require [cheffy.middlewares :as mw]
            [cheffy.recipe.api.handlers :as recipe]
            [cheffy.recipe.api.in :as in]
            [cheffy.recipe.api.out :as out]
            [cheffy.types :as types]
            [schema.core :as s])
  (:import (clojure.lang PersistentVector)))

(s/defn routes :- PersistentVector
  [env :- types/Env]
  (let [db (:jdbc-url env)]
    ["/recipes" {:swagger    {:tags ["recipes"]}
                 :middleware [[mw/wrap-auth0]]}
     [""
      {:get  {:handler   (recipe/list-all-recipes db)
              :responses {200 {:body out/Recipes}}
              :summary   "List all recipes"}
       :post {:handler    (recipe/create-recipe! db)
              :parameters {:body in/RecipeCreate}
              :responses  {201 {:body {:recipe-id s/Str}}}
              :summary    "Create recipe"}}]
     ["/:recipe-id"
      [""
       {:get    {:handler    (recipe/retrieve-recipe db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id s/Str}}
                 :responses  {200 {:body out/Recipe}}
                 :summary    "Retrieve recipe"}
        :put    {:handler    (recipe/update-recipe! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id s/Str}
                              :body in/RecipeUpdate}
                 :responses  {204 {:body nil?}}
                 :summary    "Update recipe"}
        :delete {:handler    (recipe/delete-recipe! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id s/Str}}
                 :responses  {204 {:body nil?}}
                 :summary    "Delete recipe"}}]
      ["/steps"
       {:post   {:handler    (recipe/create-step! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id s/Str}
                              :body in/StepCreate}
                 :responses  {201 {:body {:step-id s/Str}}}
                 :summary    "Create step"}
        :put    {:handler    (recipe/update-step! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id s/Str}
                              :body in/StepUpdate}
                 :responses  {204 {:body nil?}}
                 :summary    "Update step"}
        :delete {:handler    (recipe/delete-step! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id s/Str}
                              :body {:step-id s/Str}}
                 :responses  {204 {:body nil?}}
                 :summary    "Delete step"}}]
      ["/favorite"
       {:post   {:handler    (recipe/favorite-recipe! db)
                 :parameters {:path {:recipe-id s/Str}}
                 :responses  {204 {:body nil?}}
                 :summary    "Favorite recipe"}
        :delete {:handler    (recipe/unfavorite-recipe! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id s/Str}}
                 :responses  {204 {:body nil?}}
                 :summary    "Unfavorite recipe"}}]]]))
