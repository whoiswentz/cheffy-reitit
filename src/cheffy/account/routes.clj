(ns cheffy.account.routes
  (:require [cheffy.middlewares :as mw]
            [schema.core :as s]
            [cheffy.types :as types]
            [cheffy.account.handlers :as account])
  (:import (clojure.lang PersistentVector)))

(s/defn routes :- PersistentVector
  [env :- types/Env]
  (let [db (:jdbc-url env)]
    ["/accounts" {:swagger {:tags ["accounts"]}
                  :middleware [[mw/wrap-auth0]]}
     [""
      {:post {:handler (account/create-account! db)
              :response {204 {:body nil?}}
              :summary "Create account"}}]]))
