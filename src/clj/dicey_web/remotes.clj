(ns dicey-web.remotes
  (:require [dicey-web.core :refer [handler]]
            [compojure.handler :refer [site]]
            [shoreleave.middleware.rpc :refer [defremote wrap-rpc]]
            )
  )

(defremote calculate [quantity price tax discount]
  (-> (* quantity price)
      (* (+ 1 (/ tax 100)))
      (- discount)
      )
  )

(def app (-> (var handler)
             (wrap-rpc)
             (site)
             )
  )
