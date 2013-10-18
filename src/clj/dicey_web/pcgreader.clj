(ns dicey-web.pcgreader
  (:use [clojure.java.io :only [reader]]
        )
  (:require '[instaparse.core :as insta])
  )

(defn- line-filter [line]
  (> (count line) 0)
  )

(defn- remove-comments [line]
  (first (split line #"#"))
  )

(defn file-lines [filename]
  (apply str
         (interleave
          (with-open [rdr (reader filename)]
            (into [] (filter line-filter (map remove-comments (line-seq rdr))))
            )
          (repeat "\n")
          )
         )
  )

(defn- make-keyword [k]
  (keyword (.toLowerCase k))
  )

(defn- parse-int [l]
  (Integer/parseInt l)
  )

(def parsers {
              :stat (fn [l]
                      (into {}
                            (map (fn [[k v]] [k (parse-int v)])
                                 (partition 2
                                            (filter #(not= :score %)
                                                    (flatten l)
                                                    )
                                            )
                                 )
                            )
                      )
              :language (fn [l]
                          (map name
                               (filter #(not= :language %)
                                       (flatten l)
                                       )
                               )
                          )
              :weaponprof (fn [l]
                            (sort
                             (filter #(not= :weapon %)
                                     (flatten l)
                                     )
                             )
                            )
;              :ability parse-stats
;              :class parse-stats
;              :classabilitieslevel parse-stats
;              :equipset parse-stats
;              :userpool parse-stats
              :age parse-int
              :poolpoints parse-int
              :poolpointsavail parse-int
              :experience parse-int
              :height parse-int
              :weight parse-int
              :money (fn [l] (Double/parseDouble l))
              :align (fn [a]
                       (let [c1 (first a)
                             c2 (second a)
                             s1 (case c1
                                  \C "Chaotic"
                                  \T "True"
                                  \L "Lawful"
                                  "Neutral"
                                  )
                             s2 (case c2
                                  \G "Good"
                                  \E "Evil"
                                  "Neutral"
                                  )
                             ]
                         (str s1 " " s2)
                         )
                       )
              :default (fn [l] l)
              }
  )

(defn- find-parser [k]
  (first (drop-while nil? (list (k parsers) (:default parsers))))
  )

(defn parse-val [k v]
  (let [
        simplified-v (if (empty? (rest v))
                       (first v)
                       v
                       )
        ]
    ((find-parser k) simplified-v)
    )
  )

; Entire file is a map
; Everything before a : is a key
; Everything after a : is a value
; Names are keys in maps as keywords
; Values without | are lists
; Values containing | are maps
; Value maps are keyed by everything before the first |

(def charfile-parser
  (insta/parser
   "
   <character> = line*

   <kvsep> = <':'>
   <kvdelim> = <'|'>
   <linesep> = <'\n'>
   <liststart> = <'['>
   <listend> = <']'>

   <word> = #'[^:|\\[\\]\\n]+'
   <key> = word
   <val> = word | ''

   kv = key kvsep val
   listkv = key kvsep listval

   listval = liststart (kv | kvdelim)+ listend
   mapval = key (kvdelim (kv | listkv))+ kvdelim?

   <numval> = #'\\d+'
   numlist = numval (kvsep numval)+

   line = key kvsep (val | mapval | listval | numlist) linesep
   "
   )
  )

(def parsetransform
  {
   :line (fn [k & v] [(make-keyword k) (first v)])
   :kv (fn [k & v] [(make-keyword k) (first v)])
   :listval list
   :numlist vector
   :mapval (fn [k & v] [(keyword k) v])
   }
  )

(defn- keyreducer [m [k v]]
  (assoc m k (conj (k m) v))
  )

(defn parse-charfile [contents]
  (into {}
        (map (fn [[k v]] [k (parse-val k v)])
             (reduce keyreducer {}
                     (insta/transform parsetransform (charfile-parser contents))
                     )
             )
        )
  )
