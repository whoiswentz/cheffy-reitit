(ns cheffy.recipe.api.handlers
  (:require [cheffy.recipe.application.commands :as commands]
            [cheffy.recipe.application.queries :as queries]
            [cheffy.types :as types]
            [ring.util.response :as rr]
            [schema.core :as s]))

(s/defn list-all-recipes :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [uid (-> request :claims :sub)]
      (rr/response (queries/list-recipes db uid)))))

(s/defn retrieve-recipe :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)]
      (if-let [recipe (queries/get-recipe db recipe-id)]
        (rr/response recipe)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id-" recipe-id)})))))

(s/defn create-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [uid    (-> request :claims :sub)
          result (commands/create-recipe! db (-> request :parameters :body) uid)]
      (rr/created (str "localhost/recipes/" (:recipe-id result)) result))))

(s/defn update-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          changes   (-> request :parameters :body)]
      (if (commands/update-recipe! db recipe-id changes)
        (rr/status 204)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id-" recipe-id)})))))

(s/defn delete-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)]
      (if (commands/delete-recipe! db recipe-id)
        (rr/status 204)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id-" recipe-id)})))))

(s/defn favorite-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [uid       (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)]
      (commands/favorite-recipe! db recipe-id uid)
      (rr/status 204))))

(s/defn unfavorite-recipe! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [uid       (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)]
      (if (commands/unfavorite-recipe! db recipe-id uid)
        (rr/status 204)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id-" recipe-id)})))))

(s/defn create-step! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          result    (commands/create-step! db (-> request :parameters :body) recipe-id)]
      (rr/created (str "localhost/recipes/" recipe-id "/steps/" (:step-id result)) result))))

(s/defn update-step! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [changes (-> request :parameters :body)]
      (if (commands/update-step! db changes)
        (rr/status 204)
        (rr/not-found {:type    "step-not-found"
                       :message "Step not found"})))))

(s/defn delete-step! :- types/Handler
  [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [step-id (-> request :parameters :body :step-id)]
      (if (commands/delete-step! db step-id)
        (rr/status 204)
        (rr/not-found {:type    "step-not-found"
                       :message "Step not found"})))))
