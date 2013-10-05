(ns dicey-web.modern
  (:require-macros [hiccups.core :as h])
  (:require [domina :as dom]
            [domina.events :as ev]
            [hiccups.runtime :as hiccupsrt]
            [shoreleave.remotes.http-rpc :refer [remote-callback]]
            [cljs.reader :refer [read-string]]
            [goog.debug.DivConsole]
            [goog.debug.LogManager]
            [goog.events]
            [goog.events.EventType]
            [goog.object]
            [goog.ui.Button]
            [goog.ui.decorate]
            )
  )

;(.write js/document "Hello, ClojureScript!")

(defn ^:export init []
  (when (and js/document
             (aget js/document "getElementById"))
    (defn add-help [e]
      (dom/append! (dom/by-id "b1")
                   (h/html [:div.help "Click to calculate"]))
      )

    (defn remove-help [e]
      (dom/destroy! (dom/by-class "help"))
      )

    (def timer (goog.now))

    (-> (goog.debug.LogManager.getRoot)
        (.setLevel goog.debug.Logger.Level/ALL)
        )

    (def logger (goog.debug.LogManager/getLogger "demo"))
    (def logconsole (goog.debug.DivConsole. (dom/by-id "log")))
    (.setCapturing logconsole true)

    (def EVENTS (goog.object/getValues goog.ui.Component.EventType))
    (.fine logger (str "Listening for:" EVENTS "."))

    (defn logEvent [e]
      (.info logger (str "\"" (.getCaption e/target) "\" dispatched: " e/type))
      )

    (let [b1 (goog.ui.Button. "Hello!")]
      (.render b1 (dom/by-id "b1"))
      (.setTooltip b1 "New tooltip set after rendering")
      (goog.events.listen b1 EVENTS logEvent)
      (goog.events.listen (dom/by-id "b1_enable")
                          goog.events.EventType/CLICK
                          (fn [e]
                            (.setEnabled b1 e/target.checked)
                            )
                          )
      (goog.events.listen b1 goog.ui.Component.EventType/ACTION
                          (fn [e]
                            (let [newCaption (window.prompt "Enter new caption for button:")]
                              (.setCaption b1 newCaption)
                              )
                            )
                          )
      (goog.events.listen b1 goog.ui.Component.EventType/MOUSEOVER add-help)
      (goog.events.listen b1 goog.ui.Component.EventType/MOUSEOUT remove-help)
      )

    (let [b2 (goog.ui.Button.)]
      (.decorate b2 (dom/by-id "b2"))
      (goog.events.listen b2 EVENTS logEvent)
      (goog.events.listen (dom/by-id "b2_enable")
                          goog.events.EventType/CLICK
                          (fn [e]
                            (.setEnabled b2 e/target.checked)
                            )
                          )
      (goog.events.listen b2 goog.ui.Component.EventType/ACTION
                          (fn [e]
                            (js/alert (str "The value of the button is: " (.getValue b2)))
                            )
        )
      )

    (ev/listen! (dom/by-id "b1") :mouseover add-help)
    (ev/listen! (dom/by-id "b1") :mouseout remove-help)
    )
  )
