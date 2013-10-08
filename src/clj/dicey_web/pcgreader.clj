(ns dicey-web.pcgreader
  (:require [clojure.java.io :use reader]
            [clojure.string :use split]
            [instaparse.core :as insta]
            )
  )

(defn line-filter [line]
  (> (count line) 0)
  )

(defn remove-comments [line]
  (first (split line #"#"))
  )

(defn split-line [line]
  (split line #":" 2)
  )

(defn make-keywords [[k v]]
  [(keyword (.toLowerCase k)) v]
  )

(defn parse-int [l]
  (Integer/parseInt l)
  )

(defn merge-map [m [k v]]
  (if (nil? (m k))
    (assoc m k v)
    (assoc m k (conj (k m) v))
    )
  )

(defn parse-stat-line
  ([m [v & more]]
   (cond
    (nil? v)
    m

    (empty? more)
    (parse-stat-line m (split v #"\|"))

    (= -1 (.indexOf v ":"))
    (merge-map m [v (parse-stat-line {} more)])

    (not= -1 (.indexOf v ":["))
    (let [strs (split v #":" 2)
          name (first strs)
          next-strs (flatten (merge more (rest strs)))
          [s1 s2] (split-with
                   #(not (.endsWith %1 "]"))
                   next-strs
                   )
          good-strs (conj s1 (first s2))
          remaining-strs (rest s2)
          l (map #(if (.startsWith %1 "[")
                    (.substring %1 1)
                    (if (.endsWith %1 "]")
                      (.substring %1 0 (- (.length %1) 1))
                      %1
                      )
                    )
                 good-strs
                 )
          ]
      (parse-stat-line (merge-map m (make-keywords [name (parse-stat-line {} l)])) remaining-strs)
      )

    true
    (let [[name value] (split v #":" 2)]
      (parse-stat-line (merge-map m (make-keywords [name value])) more)
      )
    )
   )
  )

(defn parse-stats [l]
  (let [ls (if (list? l) l (list l))]
    (into {} (map (fn [l]
                    (parse-stat-line {} l)
                    )
                  ls
                  )
          )
    )
  )

(def parsers {
              :stat (fn [l]
                      (into {}
                            (map #(let [strs (split %1 #"[|:]")
                                        stat (keyword (.toLowerCase (first strs)))
                                        score (Integer/parseInt (second (rest strs)))
                                        ]
                                    [stat score]
                                    )
                                 l
                                 )
                            )
                      )
              :language (fn [l]
                          (filter #(not= "LANGUAGE" %1)
                                  (split l #"[\\|:]")
                                  )
                          )
              :weaponprof (fn [l]
                            (filter #(not= "WEAPON" %1)
                                    (split
                                     (replace l #"\[|\]" "")
                                     #"[\\|:]")
                                    )
                            )
              :ability parse-stats
              :class parse-stats
              :classabilitieslevel parse-stats
              :equipset parse-stats
              :userpool parse-stats
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

(defn find-parser [k]
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

(defn reduce-file [m [k v]]
  (assoc m k
    (conj (k m) v)
    )
  )

(defn file-lines [filename]
  (let [ls (with-open [rdr (reader filename)]
             (into [] (filter line-filter (map remove-comments (line-seq rdr))))
             )]
    ls
    )
  )

(defn parse-lines [lines]
  (let [pairs (map split-line lines)
        keyed-pairs (partition 2 (flatten (map make-keywords pairs)))
        reduced-pairs (reduce reduce-file {} keyed-pairs)
        ]
    (into {} (for [[k v] reduced-pairs] [k (parse-val k v)]))
    )
  )

; Entire file is a map
; Everything before a : is a key
; Everything after a : is a value
; Names are keys in maps as keywords
; Values without | are lists
; Values containing | are maps
; Value maps are keyed by everything before the first |

(def testfile "/home/ryan/pcgen6001/characters/Rajralin.pcg")

;(file-lines testfile)

(def testfilestr (apply str (interleave (file-lines testfile) (repeat "\n")))
  )

(def charfile-parser
  (insta/parser
   "
   character = line*

   <kvsep> = <':'>
   <kvdelim> = <'|'>

   <word> = #'[^:\\|\\[\\]]+'
   key = word
   val = word?

   kv = key kvsep val
   listkv = key kvsep listval

   listval = <'['> (kv | kvdelim)+  <']'>
   mapval = key (kvdelim (kv | listkv))+ kvdelim?

   line = key kvsep (val | mapval | listval) <'\n'>
   "
   )
  )

(def parsetransform
  {
   :key str
   :val str
   }
  )

(insta/transform parsetransform (charfile-parser testfilestr)
                 )

;(def char-file
;  (parse-lines (file-lines testfile))
;  )

;((:ability char-file) "FEAT")
