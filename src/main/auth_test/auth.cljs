(ns auth-test.auth
  (:require [clojure.string :as str]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [reitit.frontend.easy :as rfe]
            [auth-test.db :refer [db]])
  (:require-macros [cljs.core.async.macros :refer [go]]))


(defn submit-auth-code [code]
  (let [provider-url (-> (.-sessionStorage js/window)
                         (.getItem "provider_url"))
        client-id (-> (.-sessionStorage js/window)
                      (.getItem "client_id"))
        client-secret (-> (.-sessionStorage js/window)
                          (.getItem "client_secret"))
        token-url (str/join "/" [provider-url "oauth" "token"])]
    (go (let [api-tokens (:body (<! (http/post token-url {:form-params {:grant_type "authorization_code"
                                                                        :code code
                                                                        :client_id client-id
                                                                        :client_secret client-secret
                                                                        :redirect_uri "http://localhost:8080/oauth-callback"}
                                                          :with-credentials? false})))]
          (doto (.-sessionStorage js/window)
            (.setItem "access_token" (:access_token api-tokens))
            (.setItem "id_token" (:id_token api-tokens))
            (.setItem "refresh_token" (:refresh_token api-tokens)))

          (rfe/push-state :auth-test.router/user)))))

(defn get-userinfo []
  (let [provider-url (-> (.-sessionStorage js/window)
                         (.getItem "provider_url"))
        userinfo-url (str/join "/" [provider-url "oauth" "userinfo"])]
    (go (let [api-token (-> (.-sessionStorage js/window)
                            (.getItem "access_token"))
              userinfo (:body (<! (http/get userinfo-url {:headers {"Authorization" (str/join " " ["Bearer" api-token])}
                                                          :with-credentials? false})))]
          (js/console.log (clj->js userinfo))
          (swap! db assoc :userinfo userinfo)))))
