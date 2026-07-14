(ns cheffy.recipe.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(defn find-all
  [db uid]
  (with-open [conn (jdbc/get-connection db)]
    (cond-> {:public (sql/find-by-keys conn :recipe {:public true})}
      uid (assoc :drafts (sql/find-by-keys conn :recipe {:public false :uid uid})))))

(defn find-by-id
  [db recipe-id]
  (with-open [conn (jdbc/get-connection db)]
    (when-let [recipe (first (sql/find-by-keys conn :recipe {:recipe-id recipe-id}))]
      (assoc recipe
        :recipe/steps (sql/find-by-keys conn :step {:recipe-id recipe-id})
        :recipe/ingredients (sql/find-by-keys conn :ingredient {:recipe-id recipe-id})))))

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
