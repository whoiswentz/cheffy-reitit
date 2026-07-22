(ns cheffy.auth0
  (:require [cheffy.types :as types]
            [clj-http.client :as http]
            [integrant.repl.state :as state]
            [muuntaja.core :as m]
            [schema.core :as s]))

(s/defn auth0-url :- s/Str
  [auth0 :- types/Auth0, path :- s/Str]
  (str "https://" (:domain auth0) path))

(s/defn get-management-token :- s/Str
  [auth0 :- types/Auth0]
  (->> {:content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                        {:client_id (:client-id auth0)
                         :client_secret (:client-secret auth0)
                         :audience (auth0-url auth0 "/api/v2/")
                         :grant_type "client_credentials"})}
       (http/post (auth0-url auth0 "/oauth/token"))
       (m/decode-response-body)
       :access_token))

(defn create-auth0-user
  [{:keys [connection email password]}]
  (let [auth0 (:auth/auth0 state/system)]
    (->> {:headers {"Authorization" (str "Bearer " (get-management-token auth0))}
          :throw-exceptions false
          :content-type :json
          :cookie-policy :standard
          :body (m/encode "application/json"
                          {:connection connection
                           :email email
                           :password password})}
         (http/post (auth0-url auth0 "/api/v2/users"))
         (m/decode-response-body))))

(defn delete-user!
  [auth0 uid token]
  (http/delete (auth0-url auth0 (str "/api/v2/users/" uid))
               {:headers {"Authorization" (str "Bearer " token)}
                :throw-exceptions false}))

(defn get-role-id
  [auth0 token]
  (->> {:headers          {"Authorization" (str "Bearer " token)}
        :throw-exceptions false
        :content-type     :json
        :cookie-policy    :standard}
       (http/get (auth0-url auth0 "/api/v2/roles"))
       (m/decode-response-body)
       (filter (fn [role] (= (:name role) "Manage Recipes")))
       (first)
       :id))

(defn assign-role!
  [auth0 token uid role-id]
  (->> {:headers {"Authorization" (str "Bearer " token)}
        :throw-exceptions false
        :content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json" {:roles [role-id]})}
       (http/post (auth0-url auth0 (str "/api/v2/users/" uid "/roles")))
       (m/decode-response-body)))