(ns scratch
  (:require
   [clojure.repl :refer :all]
   [dicey-web.pcgreader :refer :all]
   [instaparse.core :as insta]
   )
  )

(def testfile "/home/ryan/pcgen6001/characters/Rajralin.pcg")

(def testcontents (file-lines testfile))

;(def char-file
;  (parse-lines (file-lines testfile))
;  )

;((:ability char-file) "FEAT")

testcontents

(def rawparse (charfile-parser testcontents))

rawparse

(map (fn [[k v]] [k (parse-val k v)])
     (parse-charfile testcontents
                         )
     )
