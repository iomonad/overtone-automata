(ns overtone-automata.patterns.bassline
  (:require [leipzig.melody :refer :all]
            [leipzig.live :as live]
            [leipzig.scale :as s]
            [overtone.live :as o]))

(def da-funk
  (->> (phrase [2 1/2 1/2 1/2 2.5 1/2 1/2 1/2 2.5 1/2 1/2 1/2 2.5 1 1]
               [0 -1 0 2 -3 -4 -3 -1 -5 -6 -5 -3 -7 -6 -5])
       (where :pitch (comp s/G s/minor))
       (all :part :da-funk)
       (all :amp 1)))

(o/definst da-funk [freq 440 dur 1.0 amp 1.0]
  (let [env (o/env-gen (o/adsr 0.3 0.7 0.5 0.3)
	               (o/line:kr 1.0 0.0 dur) :action o/FREE)
        osc (o/saw freq)]
    (-> osc (* env amp) o/pan2)))

(defmethod live/play-note :da-funk [{hertz :pitch seconds :duration amp :amp}]
  (when hertz (da-funk :freq hertz :dur seconds :amp (or amp 1))))

(->>
 da-funk
