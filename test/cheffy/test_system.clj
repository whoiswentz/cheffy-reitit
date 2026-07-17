(ns cheffy.test-system
  (:require [clojure.test :refer :all]
            [integrant.repl.state :as state]
            [ring.mock.request :as mock]
            [muuntaja.core :as m]
            [cheffy.auth0 :as auth]))
(def recipe-id (atom nil))

(def token (atom nil))

(defn token-fixtures
  [f]
  (reset! token (auth/get-test-token))
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
