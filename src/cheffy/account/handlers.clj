(ns cheffy.account.handlers
  (:require [ring.util.response :as rr]
            [schema.core :as s]
            [cheffy.account.db :as account-db]))

(s/defn create-account! [db :- cheffy.types/Database]
  (s/fn :- cheffy.types/RingResponse [request :- cheffy.types/RingRequest]
    (let [{:keys [sub name picture]} (-> request :claims)]
      (account-db/create db {:uid sub :name name :picture picture})
      (rr/status 204))))

(s/defn delete-account
  [db :- cheffy.types/Database]
  (s/fn [request]
    (let [uid (-> request :claims :sub)
          delete-auth0-account! (http/delete
                                  (str "https://dev-l6x6wetr1ruqvu3s.us.auth0.com/api/v2/users/" uid)
                                  {:header {"Authorization" (str "Bearer " (cheffy.auth0/get-management-token))}})]
      (when (= (:status delete-auth0-account!) 204)
        (account-db/delete db {:uid uid})
        (rr/status 204)))))
