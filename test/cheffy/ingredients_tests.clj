(ns cheffy.ingredients-tests
  (:require [clojure.test :refer :all]
            [cheffy.server :refer :all]
            [cheffy.test-system :as ts]))

(use-fixtures :once ts/token-fixtures)

(def recipe-id (atom nil))
(def ingredient-id (atom nil))

(def recipe
  {:img       "https://images.unsplash.com/photo-1547516508-4c1f9c7c4ec3"
   :prep-time 30
   :name      "Recipe For Ingredient Tests"})

(def ingredient
  {:name    "Flour"
   :amount  200
   :measure "grams"
   :sort    1})

(def updated-ingredient
  {:name    "Whole Wheat Flour"
   :amount  250
   :measure "grams"
   :sort    1})

(deftest ingredients-tests
  (testing "Setup - create recipe"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})]
      (reset! recipe-id (:recipe-id body))
      (is (= 201 status))))

  (testing "Create ingredient"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/ingredients")
                                                  {:auth true :body ingredient})]
      (reset! ingredient-id (:ingredient-id body))
      (is (= 201 status))))

  (testing "Update ingredient"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/ingredients")
                                             {:auth true :body (assoc updated-ingredient :ingredient-id @ingredient-id)})]
      (is (= 204 status))))

  (testing "Delete ingredient"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/ingredients")
                                             {:auth true :body {:ingredient-id @ingredient-id}})]
      (is (= 204 status))))

  (testing "Teardown - delete recipe"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
      (is (= 204 status)))))
