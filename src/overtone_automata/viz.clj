(ns overtone-automata.viz
  (:require [quil.core :as q]
            [overtone-automata.meshes.drunkards :as d]))

(defn draw []
  (let [grid (d/automata 200 200 0.37 4 4 5)
        gr (q/create-graphics 200 200)]
    (doseq [i (range 200) j (range 200)]
      (let [curr (-> grid (nth i) (nth j))]
        (println i, j)
        (cond (= curr :alive) (q/set-pixel i j (q/color 41 41 41))
              :else (q/set-pixel i j (q/color 204 204 204))))))
  (q/no-loop))

(q/defsketch drunkards
  :draw draw
  :size [200 200])
