(ns dicey-web.repl
  (:require [cljs.repl :as repl]
            [cljs.repl.browser :as browser])
  )
(def env (browser/repl-env))
(repl/repl env)
