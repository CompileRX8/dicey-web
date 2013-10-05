(defproject dicey-web "0.1.0-SNAPSHOT"
  :description "HTML UI for d20 Gaming Sessions"

  :source-paths ["src/clj"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [domina "1.0.2-SNAPSHOT"]
                 [hiccups "0.2.0"]
                 [shoreleave/shoreleave-remote-ring "0.3.0"]
                 [shoreleave/shoreleave-remote "0.3.0"]
                 ]

  :hooks [leiningen.cljsbuild]

  :plugins [
            [lein-cljsbuild "0.3.2"]
            [lein-ring "0.8.5"]
            ]

  :ring {:handler dicey-web.remotes/app}

  :cljsbuild {
              :builds {
                       :dev
                       {
                        :source-paths ["src/cljs" "src/brepl"]
                        :compiler {
                                   :output-to "resources/public/js/modern_dbg.js"
                                   :optimizations :whitespace
                                   :pretty-print true
                                   }
                        }
                       :pre-prod
                       {
                        :source-paths ["src/cljs" "src/brepl"]
                        :compiler {
                                   :output-to "resources/public/js/modern_pre.js"
                                   :optimizations :simple
                                   :pretty-print false
                                   }
                        }
                       :prod
                       {
                        :source-paths ["src/cljs"]
                        :compiler {
                                   :output-to "resources/public/js/modern.js"
                                   :optimizations :advanced
                                   :pretty-print false
                                   }
                        }
                       }
              }
  )
