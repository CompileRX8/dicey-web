(ns scratch
  (:require
   [clojure.repl :refer :all]
   [dicey-web.pcgreader :refer :all]
   [instaparse.core :as insta]
   )
  )

(def testfile "/home/ryan/pcgen6001/characters/Rajralin.pcg")

(def testcontents (file-lines testfile))

testcontents

(def rawparse (charfile-parser testcontents))

rawparse

(def xfparse (parse-charfile testcontents))

xfparse

(:ability xfparse)

(parse-val :stat (:stat xfparse))
