(ns cheffy.recipe.db
  (:require [next.jdbc.sql :as sql]
            [schema.core :as s]))

(s/defn find-all :- s/Any
  [db uid :- s/Str]
  (cond-> {:public (sql/find-by-keys db :recipe {:public true})}
    uid (assoc :drafts (sql/find-by-keys db :recipe {:public false :uid uid}))))

(defn find-by-id
  [db recipe-id]
  (when-let [recipe (first (sql/find-by-keys db :recipe {:recipe-id recipe-id}))]
    (assoc recipe
      :recipe/steps (sql/find-by-keys db :step {:recipe-id recipe-id})
      :recipe/ingredients (sql/find-by-keys db :ingredient {:recipe-id recipe-id}))))

(defn insert!
  [db recipe]
  (sql/insert! db :recipe (assoc recipe :public false)))

(defn update-recipe!
  [db recipe]
  (let [update (sql/update! db :recipe (dissoc recipe :recipe-id) (select-keys recipe [:recipe-id]))]
    (= (:next.jdbc/update-count update) 1)))

(defn delete!
  [db recipe-id]
  (let [update (sql/delete! db :recipe {:recipe-id recipe-id})]
    (= (:next.jdbc/update-count update) 1)))
