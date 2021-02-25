(ns overtone-automata.patterns.bassline
  (:require [leipzig.melody :refer :all]
            [leipzig.live :as live]
            [leipzig.scale :as scale]
            [overtone.live :as overtone]
            [overtone.synth.retro :as synths]))

(def melody
  ;; Row,  row,  row   your  boat
  (phrase [3/3   3/3   2/3   1/3   3/3]
          [  0     0     0     1     2]))

(overtone/definst beep [freq 440 dur 1.0]
  (-> freq
      overtone/saw
      (* (overtone/env-gen (overtone/perc 0.05 dur) :action overtone/FREE))))


(overtone/definst funk [freq 440 dur 1.0 amp 1.0]
  (let [env (overtone/env-gen (overtone/adsr 0.3 0.7 0.5 0.3)
                     (overtone/line:kr 1.0 0.0 dur) :action overtone/FREE)
        osc (overtone/saw freq)]
         (-> osc (* env amp) overtone/pan2)))

(defmethod live/play-note :default [{midi :pitch seconds :duration}]
  (-> midi overtone/midi->hz (funk 1 0.8)))

(def reply "The second bar of the melody."
  (phrase [2/3  1/3  2/3  1/3  6/3]
          [  2    1    2    3    4]))

(def bass "A bass part to accompany the melody."
  (->> (phrase [1  1 2]
               [0 -3 0])
       (all :part :bass)))

(defmethod live/play-note :bass [{midi :pitch}]
  ;; Halving the frequency drops the note an octave.
  (-> midi overtone/midi->hz (/ 2) (funk 0.4 2)))

(synths/tb-303 200
        :gate 5
        :cutoff 150
        :note 40
        :amp 50
        :action overtone/FREE
        :out-bus 1)

(->>
 bass
 (then (with bass melody))
 (then (with bass melody reply))
 (then (times 3 bass))
 (tempo (bpm 90))
 (where :pitch (comp scale/C scale/major))
 live/play)

(overtone/stop-all)
