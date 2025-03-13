(ns cheffy-reitit.router
  (:require [reitit.ring :as ring]
            [cheffy-reitit.recipes.routes :as recipes]))

(defn routes
  [env]
  (ring/ring-handler
    (ring/router
      [["/v1"
        (recipes/routes env)]])))