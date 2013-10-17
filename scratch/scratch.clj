(ns scratch
  (:require
   [clojure.repl :refer :all]
   [dicey-web.pcgreader :refer :all]
   [instaparse.core :as insta]
   )
  )

(def tf "/home/ryan/pcgen6001/characters/Rajralin.pcg")

;(file-lines testfile)

(def tfstr (apply str (interleave (file-lines tf) (repeat "\n")))
  )

(def tftree (charfile-parser tfstr))

tftree

(insta/transform
 parsetransform
 tftree
 )



;(def char-file
;  (parse-lines (file-lines testfile))
;  )

;((:ability char-file) "FEAT")
