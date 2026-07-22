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
  []
  (->> {:content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                        {:client_id (required-env :auth0-client-id)
                         :audience "https://dev-l6x6wetr1ruqvu3s.us.auth0.com/api/v2/"
                         :grant_type "password"
                         :username (required-env :auth0-test-username)
                         :password (required-env :auth0-test-password)
                         :scope "openid profile email"})}
       (http/post "https://dev-l6x6wetr1ruqvu3s.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))

(s/defn get-management-token :- s/Str
  []
  (->> {:content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                        {:client_id (required-env :auth0-client-id)
                         :client_secret (required-env :auth0-client-secret)
                         :audience "https://dev-l6x6wetr1ruqvu3s.us.auth0.com/api/v2/"
                         :grant_type "client_credentials"})}
       (http/post "https://dev-l6x6wetr1ruqvu3s.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))
