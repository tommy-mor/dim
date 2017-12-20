(ns dim.ref
  (:require
   [dim.helper :as h]
   [reagent.core :as r]))

(defn home-page[]
  [:div
   [:h2 "asdfasdf"]])

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
