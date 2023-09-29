(ns cheffy.swagger
  (:require [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]))

(def handler
  {:config     ["/swagger.json" {:get {:no-doc  true
                                       :swagger {:basePath "/"
                                                 :info     {:title       "Cheffy API Reference"
                                                            :description "Cheffy API"
                                                            :version     "1.0.0"}}
                                       :handler (swagger/create-swagger-handler)}}]
   :middleware swagger/swagger-feature
   :swagger-ui (swagger-ui/create-swagger-ui-handler {:path "/"})})
