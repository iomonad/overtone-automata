(ns overtone-automata.meshes.drunkards)

(comment
  "Ressources:
   - http://www.roguebasin.com/index.php?title=Random_Walk_Cave_Generation
   - http://git.slothrop.net/automata/")

(defn ^:private gen-alive [prob]
  (if (< (rand) prob)
    :alive :dead))

(defn ^:private generate-row
  [width aprob]
  (vec
   (take width (repeatedly #(gen-alive aprob)))))

(defn ^:private off-grid? [grid x y]
  (let [grid-meta (meta grid)]
    (or (< x 0)
        (>= x (-> grid-meta :width))
        (< y 0)
        (>= y (-> grid-meta :height)))))

(defn generate-grid [w h aprob]
  "Generate full grid w/ dead and alive
   cells."
  (with-meta
    (vec (take h (repeatedly #(generate-row w aprob))))
    {:width w, :height h, :prob aprob}))

(defn neighbors [grid x y]
  (for [i (range (dec x) (+ x 2))
        j (range (dec y) (+ y 2))
        :when (not= [i j] [x y])] ; Skip our cell
    (if (off-grid? grid i j) :alive
        (get-in grid [j i]))))

(defn ^:private rule-cycle-apply
  [grid x y birth-limit survival-limit]
  (let [cell-is-alive? (= (get-in grid [y x]) :alive)
        alive-neighbors (count
                          (filter #(= % :alive)
                                  (neighbors grid x y)))]
    (cond
      (and cell-is-alive?
           (>= alive-neighbors survival-limit)) :alive
      (and (not cell-is-alive?)
           (>= alive-neighbors birth-limit)) :alive
      :else :dead)))

(defn ^:private automata-stepin
  [grid birth-threshold survival-threshold]
  (let [height (-> grid meta :height)
        width (-> grid meta :width)]
    (loop [acc grid x 0 y 0]
      (if (= y height) acc
          (let [new-val (rule-cycle-apply
                         grid
                         x y
                         birth-threshold
                         survival-threshold)]
            (recur (assoc-in acc [y x] new-val)
                   (if (= (inc x) width) 0 (inc x))
                   (if (= (inc x) width) (inc y) y)))))))

(defn automata
  [width height initial-probability
   birth-threshold survival-threshold iterations]
  (nth (iterate #(automata-stepin % birth-threshold survival-threshold)
                (generate-grid width height initial-probability))
       iterations))

(automata 40 40 0.35 3 5 4)
