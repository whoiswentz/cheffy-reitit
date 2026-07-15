(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

(defn get-test-token
  []
  (->> {:content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                        {:client_id "FW8rdVKsXsITJwMxY5f53p5czJh2Favv"
                         :audience "https://dev-l6x6wetr1ruqvu3s.us.auth0.com/api/v2/"
                         :grant_type "password"
                         :username "testing@cheffy.app"
                         :password "s#m3R4nd0m-pass"
                         :scope "openid profile email"})}
       (http/post "https://dev-l6x6wetr1ruqvu3s.us.auth0.com/oauth/token")
       (m/decode-response-body)
       :access_token))
