(ns ^:figwheel-no-load dim.dev
  (:require
   [dim.ref :as core]
   [devtools.core :as devtools]))

(comment (ns ^:figwheel-no-load dim.dev
  (:require
    [dim.core :as core]
    [devtools.core :as devtools])))



(enable-console-print!)

(devtools/install!)

(core/init!)
