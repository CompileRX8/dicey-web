(ns scratch
  (:require [dicey-web.pcgreader :as pcg]
            [clojure.repl :as repl]
            [instaparse.core :as insta]
            )
  )

(def testfile "/home/ryan/pcgen6001/characters/Rajralin.pcg")

(def testcontents (pcg/file-lines testfile))

testcontents

(def rawparse (pcg/charfile-parser testcontents))

rawparse


(map (fn [[k v]] [k (pcg/parse-val k v)])
     (pcg/parse-charfile testcontents
                         )
     )
