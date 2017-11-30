(ns dim.core
  (:require
   [reagent.core :as r]))

;; -------------------------
;; Views
(defonce counter (r/atom 0))
(defonce tablemap (r/atom (sorted-map)))
(defn add-segment [number text]
  (let [id (swap! counter inc)]
    (swap! tablemap assoc id {:key id :id id :number number :text text})))

(defn interp [inp]
  (if (= inp "")
    "click to edit"
    (do
      (let [[number unit element] (clojure.string/split inp " ")]
        (reduce (fn [a b] (str a " " b)) [(or number "NUMBER") (or unit "UNIT") (or element "ELEMENT")])))))



(defn segment [item]
  (let [state (r/atom "view")
        val (r/atom "")
        to-edit #(reset! state "edit")
        to-view #(reset! state "view")]
    (fn [{:keys [id number text]}]
      (cond (= @state "view")
            [:div {:class "thinga" :on-click to-edit} (interp @val)]
            (= @state "edit")
            [:input {:key id :id id :value @val
                     :on-change #(reset! val (-> % .-target .-value))
                     :on-key-down #(case (.-which %)
                                     13 (to-view)
                                     nil)}]))))
(defn table []
  [:div.table
   (for [item (vals @tablemap)]
     [segment item])])

(defn control-component []
  [:div
   "press to add new segment: "
   [:input {:type "button" :value "click me" :on-click #(add-segment 14 "NaOH")}]
   [:br]
   "format: " [:code "NUMBER UNIT ELEMENT"]])


(defn drag-target [title func]
  [:div.dragtarget
   [:code
    title]])

;; returns a map of new item, to be added to the tablemap
;; depends on how dragging lib works
(defn drag-source [title]
  [:div.dragsource
   [:code title]])

(defn drag-hub [items]
  [:div.green
   "drag things here to manipulate"
   [:div.dragbox
    [drag-target "DELETE" #(println "delete this node" %)]
    [drag-source "NORMAL ELEMENT"]
    [drag-source "1/? ELEMENT"]
    [drag-source "?/1 ELEMENT"]
    [drag-source "EQUALS ELEMENT"]]])

(defn home-page []
  [:div [:h2 "Welcome to Reagent"]
   [control-component]
   [table]
   [drag-hub]
   ])

;; -------------------------
;; Initialize app
(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (add-segment 100 "NaOH")
  (mount-root))
