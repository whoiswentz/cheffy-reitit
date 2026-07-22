(ns cheffy.recipe.routes
  (:require [cheffy.middlewares :as mw]
            [cheffy.recipe.handlers :as recipe]
            [cheffy.recipe.in :as in]
            [cheffy.recipe.out :as out]
            [cheffy.types :as types]
            [schema.core :as s])
  (:import (clojure.lang PersistentVector)))

(s/defn routes :- PersistentVector
  [env :- types/Env]
  (let [db (:jdbc-url env)
        auth0 (:auth0 env)]
    ["/recipes" {:swagger {:tags ["recipes"]}
                 :middleware [[mw/wrap-auth0 auth0]]}
     [""
      {:get {:handler (recipe/list-all-recipes db)
             :responses {200 {:body out/Recipes}}
             :summary "List all recipes"}
       :post {:handler (recipe/create-recipe! db)
              :middleware [[mw/wrap-manage-recipes]]
              :parameters {:body in/RecipeCreate}
              :responses {201 {:body {:recipe-id s/Str}}}
              :summary "Create recipe"}}]
     ["/:recipe-id"
      [""
       {:get    {:handler    (recipe/retrieve-recipe db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id s/Str}}
                 :responses  {200 {:body out/Recipe}}
                 :summary    "Retrieve recipe"}
        :put    {:handler    (recipe/update-recipe! db)
                 :middleware [[mw/wrap-recipe-owner db]
                              [mw/wrap-manage-recipes]]
                 :parameters {:path {:recipe-id s/Str}
                              :body in/RecipeUpdate}
                 :responses  {204 {:body nil?}}
                 :summary    "Update recipe"}
        :delete {:handler    (recipe/delete-recipe! db)
                 :middleware [[mw/wrap-recipe-owner db]
                              [mw/wrap-manage-recipes]]
                 :parameters {:path {:recipe-id s/Str}}
                 :responses  {204 {:body nil?}}
                 :summary    "Delete recipe"}}]
      ["/steps"
       {:middleware [[mw/wrap-recipe-owner db]
                     [mw/wrap-manage-recipes]]}
       [""
        {:post   {:handler    (recipe/create-step! db)
                  :parameters {:path {:recipe-id s/Str}
                               :body in/StepCreate}
                  :responses  {201 {:body {:step-id s/Str}}}
                  :summary    "Create step"}
         :put    {:handler    (recipe/update-step! db)
                  :parameters {:path {:recipe-id s/Str}
                               :body in/StepUpdate}
                  :responses  {204 {:body nil?}}
                  :summary    "Update step"}
         :delete {:handler    (recipe/delete-step! db)
                  :parameters {:path {:recipe-id s/Str}
                               :body {:step-id s/Str}}
                  :responses  {204 {:body nil?}}
                  :summary    "Delete step"}}]]
      ["/ingredients"
       {:middleware [[mw/wrap-recipe-owner db]
                     [mw/wrap-manage-recipes]]}
       [""
        {:post   {:handler    (recipe/create-ingredient! db)
                  :parameters {:path {:recipe-id s/Str}
                               :body in/IngredientCreate}
                  :responses  {201 {:body {:ingredient-id s/Str}}}
                  :summary    "Create ingredient"}
         :put    {:handler    (recipe/update-ingredient! db)
                  :parameters {:path {:recipe-id s/Str}
                               :body in/IngredientUpdate}
                  :responses  {204 {:body nil?}}
                  :summary    "Update ingredient"}
         :delete {:handler    (recipe/delete-ingredient! db)
                  :parameters {:path {:recipe-id s/Str}
                               :body {:ingredient-id s/Str}}
                  :responses  {204 {:body nil?}}
                  :summary    "Delete ingredient"}}]]
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
