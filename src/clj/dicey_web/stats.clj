(ns dicey-web.stats
  )

(def character-stats {
                      :ability {
                                :str 12
                                :dex 14
                                :con 12
                                :int 13
                                :wis 8
                                :cha 15
                                }
                      :saves {
                              :fortitude 2
                              :reflex 2
                              :will 5
                              }
                      }
  )

(defn ability-mod [a]
  (- (quot a 2) 5)
  )

(defn mod-str [m]
  (str (if (< m 0) "" "+") m)
  )

(defn apply-mods []
  )
