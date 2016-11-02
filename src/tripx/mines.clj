(defn val-at
  "given a grid of points, which is a list of lists,
   return the value at the r,c point pt"
  [grid pt]
  (let [row-num (first pt)
        col-num (second pt)
        row (nth grid row-num)]
    (nth row col-num)))

(defn neighbors
  "given a point r,c and max dimension of grid n,
   return a list of all the r,c points that are neighbors of point r,c.
   start with all points r-1 to r+1, c-1 to c+1, then remove any points
   with negative (out of the grid), > dimension n size,  and the point itself"
  [r c n]
  (for [r1 (range (dec r) (+ 2 r))
        c1 (range (dec c) (+ 2 c)) :when (and (>= r1 0)
                                              (< r1 n)
                                              (>= c1 0)
                                              (< c1 n)
                                              (not (and (= r1 r)
                                                        (= c1 c))))]
    [r1 c1]))

(defn total
  "take in grid - a list of lists containing 0/1 indicating no mine/ mine
   and pt is a point r,c for which we will calculate the total number
   of mines surrounding that point by adding up the values of the neighbors"
  [grid dimension pt]
  (let [n (neighbors (first pt) (second pt) dimension)
        v (partial val-at grid)]
    (reduce + (map v n))))

(defn count-mines
  "take in array of integers, first is size of nxn grid,
   after that comes nxn digits, either 0 or 1 (mine / no mine)."
  [in]
  (let [dimension (first in)
        grid (partition dimension (rest in))
        pts (for [r (range 0 dimension)
                  c (range 0 dimension)] [r c])
        tot (partial total grid dimension)]
    (map tot pts)))
