(ns dim.core
  (:require
   [reagent.core :as r]))

;; -------------------------
;; Views
(def counter (r/atom 0))
(defonce table (r/atom (sorted-map)))

(defn add-segment [number text]
  (let [id (swap! counter inc)]
    (swap! table assoc id {:id id :number number :text text})))

(defn interp [inp]
  (if (= inp "")
    "click to edit"
    (do
      (let [[number unit element] (clojure.string/split inp ",")]
        (fn []
          (reduce (fn [a b] (str a " " b)) [(or number "NUMBER") (or unit "UNIT") (or element "ELEMENT")]))))))

(defn segment [item]
  (let [state (r/atom "view")
        val (r/atom "")
        to-edit #(reset! state "edit")
        to-view #(reset! state "view")]
    (fn [{:keys [id number text]}]
      (cond (= @state "view")
            [:div {:class "thinga" :on-click to-edit} [interp @val]]
            (= @state "edit")
            [:input {:key id :id id :value @val
                     :on-change #(reset! val (-> % .-target .-value))
                     :on-key-down #(case (.-which %)
                                     13 (to-view)
                                     nil)}]))))

(defn lister []
  [:ul
   (for [item (vals @table)]
     [:li [segment item]])])

(defn control-component []
  [:div
   "press to add new segment: "
   [:input {:type "button" :value "click me" :on-click #((add-segment 14 "NaOH"))}]])

(defn home-page []
  [:div [:h2 "Welcome to Reagent"]
   [control-component]
   [lister]])

;; -------------------------
;; Initialize app
(add-segment 100 "NaOH")

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
