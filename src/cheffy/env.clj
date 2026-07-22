(ns cheffy.env
  (:require [environ.core :refer [env]]
            [schema.core :as s]))

(s/defn required! :- s/Str
  [k :- s/Keyword]
  (or (env k)
      (throw
       (ex-info (str "Missing environment variable: " k) {:key k}))))
