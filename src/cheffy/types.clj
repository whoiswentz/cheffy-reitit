(ns cheffy.types
  (:require [schema.core :as s]
            [next.jdbc.protocols :as p])
  (:import (clojure.lang IPersistentMap)
           (java.io InputStream)))

(def Database (s/protocol p/Executable))

(def RingRequest
  {:server-port                        s/Int
   :server-name                        s/Str
   :remote-addr                        s/Str
   :uri                                s/Str
   (s/optional-key :query-string)      s/Str
   :scheme                             (s/enum :http :https)
   :request-method                     (s/enum :get :post :put :delete :head :options :patch)
   (s/optional-key :headers)           {s/Str s/Str}
   (s/optional-key :body)              InputStream
   (s/optional-key :muuntaja/request)  IPersistentMap
   (s/optional-key :muuntaja/response) IPersistentMap
   (s/optional-key :parameters)        {(s/optional-key :path)  IPersistentMap
                                        (s/optional-key :query) IPersistentMap
                                        (s/optional-key :body)  IPersistentMap}
   (s/optional-key :claims)            {s/Keyword Object}
   s/Keyword                           Object})

(def RingResponse
  {:status                   s/Int
   (s/optional-key :headers) {s/Str s/Str}
   (s/optional-key :body)    Object
   s/Keyword                 Object})

(def Handler
  (s/=> RingResponse RingRequest))

(def AuthMiddleware
  {:name                         s/Keyword
   (s/optional-key :description) s/Str
   :wrap                         (s/=> Handler Handler)})

(def OwnerMiddleware
  {:name                         s/Keyword
   (s/optional-key :description) s/Str
   :wrap                         (s/=> Handler Handler Database)})

(def Auth0
  {:domain        s/Str
   :client-id     s/Str
   :client-secret s/Str})

(def Env
  {:jdbc-url Database
   :auth0    Auth0})
