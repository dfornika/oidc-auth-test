(ns auth-test.router
  (:require [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.frontend.controllers :as rfc]
            [auth-test.views :as views]
            [auth-test.auth :as auth]))

(def routes
  [["/" {:name ::root
         :view #'views/login}]

   ["/oauth-callback" {:name ::oauth-callback
                       :view #'views/auth
                       :parameters {:query [:code]}
                       :controllers [{:parameters {:query [:code]}
                                      :start (fn [parameters] (auth/submit-auth-code (-> parameters :query :code)))}]}]
   ["/user" {:name ::user
             :view #'views/user
             :controllers [{:start auth/get-userinfo}]}]
             
   ["/logout"   {:name ::logout}]])

(def router
  (rf/router routes {:default-view #'views/loading}))


(defn on-navigate
  ""
  [db new-match history] 
  (swap! db (fn [old-db] 
              (when new-match
                (assoc old-db :route-match
                       (assoc new-match :controllers
                              (rfc/apply-controllers (get-in old-db [:route-match :controllers]) new-match)))))))


(defn start!
  ""
  [db]
  (rfe/start! router (partial on-navigate db) {:use-fragment false}))
