(ns auth-test.core
  (:require [reagent.dom]
            [auth-test.router :as router]
            [auth-test.views :as views]
            [auth-test.db :refer [db]]))

(defn ^:export init
  []
  (router/start! db)
  (reagent.dom/render [views/root {:db db :router router/router}]
    (.getElementById js/document "app")))
