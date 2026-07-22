(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [environ.core :refer [env]]
            [muuntaja.core :as m]
            [schema.core :as s]))

(s/defn ^:private required-env :- s/Str
  [k :- s/Keyword]
  (or (env k)
      (throw
       (ex-info (str "Missing environment variable: " k) {:key k}))))

(s/defn get-test-token :- s/Str
  ([] (get-test-token (required-env :auth0-test-username)))
  ([username :- s/Str]
   (->> {:content-type :json
         :cookie-policy :standard
         :body (m/encode "application/json"
                         {:client_id (required-env :auth0-test-client-id)
                          :audience "https://dev-kvt13fczy54wnqui.us.auth0.com/api/v2/"
                          :grant_type "http://auth0.com/oauth/grant-type/password-realm"
                          :realm "Username-Password-Authentication"
                          :username username
                          :password (required-env :auth0-test-password)
                          :scope "openid profile email"})}
        (http/post "https://dev-kvt13fczy54wnqui.us.auth0.com/oauth/token")
        (m/decode-response-body)
        :access_token)))

(s/defn get-management-token :- s/Str
  []
  (->> {:content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                        {:client_id (required-env :auth0-client-id)
                         :client_secret (required-env :auth0-client-secret)
                         :audience "https://dev-kvt13fczy54wnqui.us.auth0.com/api/v2/"
                         :grant_type "client_credentials"})}
       (http/post "https://dev-kvt13fczy54wnqui.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))

(defn create-auth0-user
  [{:keys [connection email password]}]
  (->> {:headers {"Authorization" (str "Bearer " (get-management-token))}
        :throw-exceptions false
        :content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                        {:connection connection
                         :email email
                         :password password})}
       (http/post "https://dev-kvt13fczy54wnqui.us.auth0.com/api/v2/users")
       (m/decode-response-body)))

(defn get-role-id
  [token]
  (->> {:headers          {"Authorization" (str "Bearer " token)}
        :throw-exceptions false
        :content-type     :json
        :cookie-policy    :standard}
       (http/get "https://dev-kvt13fczy54wnqui.us.auth0.com/api/v2/roles")
       (m/decode-response-body)
       (filter (fn [role] (= (:name role) "Manage Recipes")))
       (first)
       :id))

(defn assign-role!
  [token uid role-id]
  (->> {:headers {"Authorization" (str "Bearer " token)}
        :throw-exceptions false
        :content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json" {:roles [role-id]})}
       (http/post (str "https://dev-kvt13fczy54wnqui.us.auth0.com/api/v2/users/" uid "/roles"))
       (m/decode-response-body)))