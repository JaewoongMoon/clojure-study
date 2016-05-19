
;
(defn copy-tri*
  ([](copy-tri* 0 1))
  ([sum n]
   (let [new-sum (+ sum n)]
     (cons new-sum (lazy-seq (copy-tri* new-sum (inc n))))))) 

(def copy-tri (copy-tri*))

(take 5 copy-tri)



