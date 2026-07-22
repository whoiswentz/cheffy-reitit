(ns cheffy.account.handlers
  (:require [cheffy.account.db :as account-db]
            [cheffy.auth0 :as auth0]
            [cheffy.types :as types]
            [ring.util.response :as rr]
            [schema.core :as s]))

(s/defn create-account! [db :- types/Database]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [{:keys [sub name picture]} (-> request :claims)]
      (account-db/create db {:uid sub :name name :picture picture})
      (rr/status 204))))

(s/defn delete-account! :- types/Handler
  [db :- types/Database, auth0 :- types/Auth0]
  (s/fn :- types/RingResponse [request :- types/RingRequest]
    (let [uid (-> request :claims :sub)
          response (auth0/delete-user! auth0 uid (auth0/get-management-token auth0))]
      (if (= (:status response) 204)
        (do (account-db/delete db {:uid uid})
            (rr/status 204))
        (-> (rr/response {:type "auth0-account-deletion-failed"
                          :message "Failed to delete account on Auth0"
                          :data (str "uid-" uid)})
            (rr/status 502))))))

(defn update-role-to-cook!
  [auth0]
  (fn [request]
    (let [uid (-> request :claims :sub)
          token (auth0/get-management-token auth0)
          role-id (auth0/get-role-id auth0 token)]
      (auth0/assign-role! auth0 token uid role-id)
      (rr/status 204))))
