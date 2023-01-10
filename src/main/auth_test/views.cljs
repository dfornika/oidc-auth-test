(ns auth-test.views
  (:require [auth-test.db :refer [db]]
            [clojure.string :as str]
            [reitit.core :as reitit]))


(defn loading
  "To show before routes are loaded"
  []
  [:h1 "Loading..."])


(defn auth []
  [:h1 "Submitting Authentication Code..."])


(defn root [{:keys [db router]}]
  (let [route (:route-match @db)]
    (if (some? route)
      (when-let [view (get-in route [:data :view])]
        [view])
      (-> router reitit/options :default-view))))


(defn login-form
  ""
  []
  [:div {:class "border border-primary rounded"}
   [:form {:class "form-vertical" :style {:width "36rem"}}
    [:div {:class "form-group" :style {:margin "10px"}}
     [:label {:for :oidc-provider-url} "OIDC Authorization Endpoint URL"]
     [:input {:name :oidc-provier-url :class "form-control" :type "text"
              :value (get-in @db [:oidc-config :authorization-endpoint])
              :on-change (fn [e]
                           (let [value (-> e .-target .-value)]
                             (-> (.-sessionStorage js/window)
                                 (.setItem "oidc_authorization_endpoint" value))
                             (swap! db assoc-in [:oidc-config :authorization-endpoint] value)))}]]
    [:div {:class "form-group" :style {:margin "10px"}}
     [:label {:for :client-id} "Client ID"]
     [:input {:name :client-id :class "form-control" :type "text"
              :value (get-in @db [:oidc-config :client-id])
              :on-change (fn [e]
                           (let [value (-> e .-target .-value)]
                             (-> (.-sessionStorage js/window)
                                 (.setItem "oidc_client_id" value))
                             (swap! db assoc-in [:oidc-config :client-id] value)))}]]
    [:div {:class "form-group" :style {:margin "10px"}}
     [:label {:for :client-secret} "Client Secret"]
     [:input {:name :client-secret :class "form-control" :type "text"
              :value (get-in @db [:oidc-config :client-secret])
              :on-change (fn [e]
                           (let [value (-> e .-target .-value)]
                             (-> (.-sessionStorage js/window)
                                 (.setItem "oidc_client_secret" value))
                             (swap! db assoc-in [:oidc-config :client-secret] value)))}]]
    [:div {:class "form-group" :style {:margin "10px"}}
     [:label {:for :oidc-provider-url} "Scopes"]
     [:input {:name :oidc-provier-url :class "form-control" :type "text"
              :value (get-in @db [:oidc-config :scope])
              :on-change (fn [e]
                           (let [value (-> e .-target .-value)]
                             (-> (.-sessionStorage js/window)
                                 (.setItem "oidc_scope" value))
                             (swap! db assoc-in [:oidc-config :scope] value)))}]]
    [:a {:class "btn btn-primary"
         :style {:margin "10px"}
         :href (str (get-in @db [:oidc-config :authorization-endpoint])
                    "/?response_type=code"
                    "&client_id=" (get-in @db [:oidc-config :client-id])
                    "&scope=" "openid"
                    "&redirect_uri=http://localhost:8081/oauth-callback")}
     "Login"]]])

(defn login
  ""
  []
  [:div {:class "vh-100 d-flex justify-content-center align-items-center"}
   [login-form "" "" ""]])

(defn user-card
  ""
  []
  [:div {:class "card" :style {:width "18rem"}}
   [:img {:class "card-img-top" :src (get-in @db [:userinfo :picture])}]
   [:ul {:class "list-group list-group-flush"}
    [:li {:class "list-group-item"} (str "Name: " (get-in @db [:userinfo :name]))]
    [:li {:class "list-group-item"} (str "Username: " (get-in @db [:userinfo :nickname]))]]])

(defn user
  ""
  []
  [:div {:class "vh-100 d-flex justify-content-center align-items-center"}
   [user-card]])