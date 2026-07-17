(ns cheffy.steps-tests
  (:require [clojure.test :refer :all]
            [cheffy.server :refer :all]
            [cheffy.test-system :as ts]))

(use-fixtures :once ts/token-fixtures)

(def recipe-id (atom nil))
(def step-id (atom nil))

(def recipe
  {:img       "https://images.unsplash.com/photo-1547516508-4c1f9c7c4ec3"
   :prep-time 30
   :name      "Recipe For Step Tests"})

(def step
  {:description "Mix the ingredients"
   :sort        1})

(def updated-step
  {:description "Mix the ingredients thoroughly"
   :sort        1})

(deftest steps-tests
  (testing "Setup - create recipe"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})]
      (reset! recipe-id (:recipe-id body))
      (is (= 201 status))))

  (testing "Create step"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/steps")
                                                  {:auth true :body step})]
      (reset! step-id (:step-id body))
      (is (= 201 status))))

  (testing "Update step"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/steps")
                                             {:auth true :body (assoc updated-step :step-id @step-id)})]
      (is (= 204 status))))

  (testing "Delete step"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/steps")
                                             {:auth true :body {:step-id @step-id}})]
      (is (= 204 status))))

  (testing "Teardown - delete recipe"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
      (is (= 204 status)))))
