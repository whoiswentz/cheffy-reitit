(ns cheffy.account.db
  (:require [next.jdbc.sql :as sql]
            [schema.core :as s]))

(s/defn create
  [db :- cheffy.types/Database account]
  (sql/insert! db :account account))

(s/defn delete
  [db uid]
  (sql/delete! db :account uid))
