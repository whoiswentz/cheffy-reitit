(ns cheffy.test-system
  (:require [cheffy.auth0 :as auth]
            [cheffy.env :as env]
            [clj-http.client :as http]
            [clojure.test :refer :all]
            [integrant.repl.state :as state]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]
            [schema.core :as s]))

(def recipe-id (atom nil))

(def token (atom nil))

(defn auth0-config
  []
  (:auth/auth0 state/system))

(s/defn get-test-token :- s/Str
  ([] (get-test-token (env/required! :auth0-test-username)))
  ([username :- s/Str]
   (let [auth0 (auth0-config)]
     (->> {:content-type :json
           :cookie-policy :standard
           :body (m/encode "application/json"
                           {:client_id (env/required! :auth0-test-client-id)
                            :audience (auth/auth0-url auth0 "/api/v2/")
                            :grant_type "http://auth0.com/oauth/grant-type/password-realm"
                            :realm "Username-Password-Authentication"
                            :username username
                            :password (env/required! :auth0-test-password)
                            :scope "openid profile email"})}
          (http/post (auth/auth0-url auth0 "/oauth/token"))
          (m/decode-response-body)
          :access_token))))

(defn account-fixture
  [f]
  (auth/create-auth0-user
   {:connection "Username-Password-Authentication"
    :email "account-tests@cheffy.app"
    :password "s#m3R4nd0m-pass"})
  (reset! token (get-test-token "account-tests@cheffy.app"))
  (f))

(defn create-auth0-test-user
  [user]
  (auth/create-auth0-user user))

(defn token-fixtures
  [f]
  (reset! token (get-test-token))
  (f)
  (reset! token nil))

(defn test-endpoint
  ([method uri]
   (test-endpoint method uri nil))
  ([method uri opts]
   (let [app (-> state/system :cheffy/app)
         request (app (-> (mock/request method uri)
                          (cond-> (:auth opts) (mock/header :authorization (str "Bearer " @token))
                                  (:body opts) (mock/json-body (:body opts)))))]
     (update request :body (partial m/decode "application/json")))))
