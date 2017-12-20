(ns dim.core
  (:require
   [dim.helper :as h]
   [reagent.core :as r]))

;; maybe to solve function call issue, use something like re-frame events to handle it correctly
;; maybe use re-frame, or use on of the recipies on ghub.


;; -------------------------
;; Views
(defn log [a] (js/console.log a))
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
    ; number, key, text never used
    ; confused by what this is being called with
    (fn [a [{:keys [id key number text]}]]
      (cond (= @state "view")
            [:div {:key id :id id :class "thinga" :on-click to-edit} id (interp @val)]
            (= @state "edit")
            [:input {:key nil :id nil :value @val
                     :on-change #(reset! val (-> % .-target .-value))
                     :on-key-down #(case (.-which %)
                                     13 (do (to-view) (comment "TODO update tablemap not just ratom"))
                                     nil)}]))))
(defn table []
  [:div#table.table
   (for [item (vals @tablemap)]
     [h/draggable-component segment item])])

(defn control-component []
  [:div
   "press to add new segment: "
   [:input {:type "button" :value "click me" :on-click #(add-segment 14 "NaOH")}]
   [:input {:type "button" :value "click me" :on-click #(log @tablemap)}]
   [:br]
   "format: " [:code "NUMBER UNIT ELEMENT"]])

;; returns a map of new item, to be added to the tablemap
;; depends on how dragging lib works

(def action-to-display {"normal" "NORMAL ELEMENT"
                        "under" "1/? ELEMENT"
                        "over" "?/1 ELEMENT"
                        "equals" "EQUALS ELEMENT"})

(defn drag-target [title]
  [:div.dragtarget {:id title}
   [:code title]])

(defn drag-source [title]
  [:div {:id title :class "dragsource"}
   [:code (get action-to-display title)]])

; TODO try to understand mmap example on reagent.io
(defn handle-delete-event [event draggable]
  (let [id (as-> draggable x (.-draggable x) (aget x "0") (.-id x))]
    (log id)
    (swap! tablemap dissoc (int id))
    (log tablemap)))

(defn handle-event [event draggable]
  (let [id (as-> draggable x (.-draggable x) (aget x "0") (.-id x))]
    (case id
      "normal" (add-segment 15 "normals")
      "under" (add-segment 15 "unders")
      "over" (add-segment 15 "overs")
      "equals" (add-segment 15 "equals")
      (println "you silly goose"))))

(defn drag-hub [items]
  [:div.green
   "drag things here to manipulate"
   [:div.dragbox
    [h/droppable-component drag-target handle-delete-event "DELETE"]
    [h/draggable-component drag-source "normal"]
    [h/draggable-component drag-source "under"]
    [h/draggable-component drag-source "over"]
    [h/draggable-component drag-source "equals"]]])

(defn home-page []
  [:div [:h2 "Welcome to The Formatter"]
                                        ;[h/droppable-component table handle-event]
   [control-component]
   [table handle-event]
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
