(ns dim.core
  (:require
   [dim.helper :as h]
   [reagent.core :as r]))

;; maybe to solve function call issue, use something like re-frame events to handle it correctly
;; maybe use re-frame, or use on of the recipies on ghub.


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
  [:div#table.table
   (for [item (vals @tablemap)]
     [segment item])])

(defn control-component []
  [:div
   "press to add new segment: "
   [:input {:type "button" :value "click me" :on-click #(add-segment 14 "NaOH")}]
   [:br]
   "format: " [:code "NUMBER UNIT ELEMENT"]])


(defn drag-target [title]
  [:div.dragtarget
   [:code
    title]])

;; returns a map of new item, to be added to the tablemap
;; depends on how dragging lib works

(def action-to-display {"normal" "NORMAL ELEMENT"
                        "under" "1/? ELEMENT"
                        "over" "?/1 ELEMENT"
                        "equals" "EQUALS ELEMENT"})

(defn drag-source [title]
  [:div {:id title :class "dragsource"}
   [:code (get action-to-display title)]])

(defn drag-hub [items]
  [:div.green
   "drag things here to manipulate"
   [:div.dragbox
    [drag-target "DELETE"]
    [h/draggable-component drag-source "normal"]
    [h/draggable-component drag-source "under"]
    [h/draggable-component drag-source "over"]
    [h/draggable-component drag-source "equals"]]])

(defn handle-event [event draggable]
  (let [id (as-> draggable x (.-draggable x) (aget x "0") (.-id x))]
    "dddddd"
    (case id
      "normal" (add-segment 15 "normals")
      "under" (add-segment 15 "unders")
      "over" (add-segment 15 "overs")
      "equals" (add-segment 15 "equals"))))

(defn home-page []
  [:div [:h2 "Welcome to The Formatter"]
   [control-component]
   [h/droppable-component table handle-event]
   [drag-hub]])

(defn box [title]
  [:div {:style {:background-color "pink" :width "150px" :height "150px" :padding "0.5em"}}
   [:p title]])


;; -------------------------
;; Initialize app

;;drag test
(comment (defn home-page []
           [:div
            [draggable-component box "title of this box"]
            [draggable-component box "title of this box"]
            [draggable-component box "title of this box"]]))

(defn mount-root []
  (r/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (add-segment 100 "NaOH")
  (mount-root))
