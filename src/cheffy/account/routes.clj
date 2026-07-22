(ns cheffy.account.routes
  (:require [cheffy.account.handlers :as account]
            [cheffy.middlewares :as mw]
            [cheffy.types :as types]
            [schema.core :as s])
  (:import (clojure.lang PersistentVector)))

(s/defn routes :- PersistentVector
  [env :- types/Env]
  (let [db (:jdbc-url env)
        auth0 (:auth0 env)]
    ["/account" {:swagger {:tags ["accounts"]}
                 :middleware [[mw/wrap-auth0 auth0]]}
     [""
      {:post {:handler (account/create-account! db)
              :responses {204 {:body nil?}}
              :summary "Create account"}
       :put  {:handler   (account/update-role-to-cook! auth0)
              :responses {204 {:body nil?}}
              :summary   "Update user role to cook"}
       :delete {:handler   (account/delete-account! db auth0)
                :responses {204 {:body nil?}}
                :summary   "Delete account"}}]]))
