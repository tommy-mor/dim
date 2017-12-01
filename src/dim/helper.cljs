(ns dim.helper
  (:require
   [reagent.core :as r]))


(defn draggable-maker [this]
  (let [f #js {:opacity 0.7 :helper "clone"}]
    (.draggable (js/$ (r/dom-node this)) f)))

;; render must be function
(defn draggable-component [render & args]
  (r/create-class {:reagent-render (fn [] [apply render args]) :component-did-mount draggable-maker}))

(defn droppable-maker [this dropfn]
  (.droppable (js/$ (r/dom-node this))
              #js {:drop dropfn}))

;; render must be function
(defn droppable-component [render dropfn & args]
  (r/create-class {:reagent-render (fn [] [apply render args]) :component-did-mount #(droppable-maker % dropfn)}))
