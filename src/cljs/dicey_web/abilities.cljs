(ns dicey-web.abilities
;  (:require-macros [hiccups.core :as h])
  (:require [domina :as dom]
            ;[domina.events :as ev]
            ;[hiccups.runtime :as hiccupsrt]
            ;[shoreleave.remotes.http-rpc :refer [remote-callback]]
            [cljs.reader :refer [read-string]]
;            [goog.debug.DivConsole]
;            [goog.debug.LogManager]
;            [goog.events]
;            [goog.events.EventType]
;            [goog.object]
;            [goog.ui.Button]
;            [goog.ui.decorate]
;            [goog.ui.RoundedPanel]
;            [goog.ui.Container]
            [goog.dom]
            )
  )

(defn ^:export init []
  (when (and js/document
             (aget js/document "getElementById"))
    (dom/set-value! (dom/by-id "ability-str") "14")
    (dom/set-value! (dom/by-id "ability-dex") "17")
    (dom/set-value! (dom/by-id "ability-con") "13")
    (dom/set-value! (dom/by-id "ability-int") "15")
    (dom/set-value! (dom/by-id "ability-wis") "12")
    (dom/set-value! (dom/by-id "ability-cha") "7")
    )
  )
