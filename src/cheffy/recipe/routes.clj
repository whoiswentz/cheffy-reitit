(ns cheffy.recipe.routes
  (:require [cheffy.recipe.handlers :as recipe]
            [cheffy.responses :as responses]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/recipes" {:swagger {:tags ["recipes"]}}
     [""
      {:get {:handler (recipe/list-all-recipes db)
             :response {200 {:body responses/recipe}}
             :summary "List all recipes"}}
      :post {:handler (recipe/create-recipe! db)
             :parameter {:body {:name string?
                                :prep-time number?
                                :img string?}}
             :response {201 {:body {:recipe-id string?}}}
             :summary "Create recipe"}]
     ["/:recipe-id"
      {:get {:handler (recipe/retrieve-recipe db)
             :parameters {:path {:recipe-id string?}}
             :response {200 {:body responses/recipe}}
             :summary "Retrieve recipe"}}
      :put {:handler   (recipe/update-recipe! db)
            :parameter {:path {:recipe-id string?}
                        :body {:name      string?
                               :prep-time int?
                               :public    boolean?
                               :img       string?}}
            :responses {204 {:body nil?}}
            :summary   "Update recipe"}
      :delete {:handler (recipe/delete-recipe! db)
            :parameters {:path {:recipe-id string?}}
            :response {204 {:body nil?}}
            :summary "Delete recipe"}]]))
