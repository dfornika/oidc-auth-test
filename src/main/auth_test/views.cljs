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
  [:form {:style {:width "36rem"}}
   [:div {:class "form-group"}
    [:label {:for :oidc-provider-url} "OIDC Provider URL"]
    [:input {:name :oidc-provier-url :class "form-control" :type "text"
             :value (get-in @db [:oidc-config :provider-url])
             :on-change (fn [e]
                          (let [value (-> e .-target .-value)]
                            (-> (.-sessionStorage js/window)
                                (.setItem "provider_url" value))
                            (swap! db assoc-in [:oidc-config :provider-url] value)))}]]
   [:div {:class "form-group"}
    [:label {:for :client-id} "Client ID"]
    [:input {:name :client-id :class "form-control" :type "text"
             :value (get-in @db [:oidc-config :client-id])
             :on-change (fn [e]
                          (let [value (-> e .-target .-value)]
                            (-> (.-sessionStorage js/window)
                                (.setItem "client_id" value))
                            (swap! db assoc-in [:oidc-config :client-id] value)))}]]
   [:div {:class "form-group"}
    [:label {:for :client-secret} "Client Secret"]
    [:input {:name :client-secret :class "form-control" :type "text"
             :value (get-in @db [:oidc-config :client-secret])
             :on-change (fn [e]
                          (let [value (-> e .-target .-value)]
                            (-> (.-sessionStorage js/window)
                                (.setItem "client_secret" value))
                            (swap! db assoc-in [:oidc-config :client-secret] value)))}]]
   [:a {:class "btn btn-primary"
        :href (str (str/join "/" [(-> (.-sessionStorage js/window)
                                      (.getItem "provider_url")) "oauth" "authorize"])
                   "?response_type=code"
                   "&client_id=" (-> (.-sessionStorage js/window)
                                     (.getItem "client_id"))
                   "&scope=openid read_api"
                   "&redirect_uri=http://localhost:8080/oauth-callback")}
    "Login"]])

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