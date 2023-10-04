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
             :responses {200 {:body responses/recipes}}}
       :post {:handler    (recipe/create-recipe! db)
              :summary    "create recipe"
              :parameters {:body {:name      string?
                                  :prep-time number?
                                  :img       string?}}
              :responses  {201 {:body {:recipe-id string?}}}}}]
     ["/:recipe-id"
      {:get {:handler    (recipe/retrieve-recipe db)
             :parameters {:path {:recipe-id string?}}
             :summary    "list by id"
             :responses  {200 {:body responses/recipe}}}
       :put {:handler    (recipe/update-recipe! db)
             :summary    "update recipe"
             :parameters {:path {:recipe-id string?}
                          :body {:name      string?
                                 :prep-time int?
                                 :public    boolean?
                                 :img       string?}}
             :responses  {200 {:body responses/recipe}}}
       :delete {:handler    (recipe/delete-recipe! db)
                :summary    "delete recipe"
                :parameters {:path {:recipe-id string?}}
                :responses  {204 {:body nil?}}}}]]))