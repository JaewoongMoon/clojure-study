(ns euler-project.core
  (:gen-class))

; ex1) 1000 보다 작은 자연수 중에서 3 또는 5의 배수를 모두 더하면?

(defn make-list
  [max-num]
  (loop [num 0 result []]
    (println (str "result :" result))
    (if (>= num max-num)
      result
      (recur (inc num) (conj result num)))))

(make-list 10)

(defn is-multiple?
  ([num]
   (or (= (mod num 3) 0) (= (mod num 5) 0))))

(reduce + (set (map (fn [num] (if (is-multiple? num) num 0) ) 
                    (make-list 1000))))

; ex2) 피보나치 수열에서 사백만 이하이면서 짝수인 항의 합
(defn fib [n]
  (cond 
   (= n 0) 1
   (= n 1) 2
   :else (+ (fib (- n 1)) (fib (- n 2)) )
))

(defn fib-list 
  [until]
  (loop [fib-val (fib 0) iter 0 result [] ]
    (if (>= fib-val until)
      result
      (recur (fib iter) (inc iter) (conj result fib-val) ))))

(reduce +  (map (fn [num] (if (= (mod num 2) 0) num 0)) 
        (fib-list 4000000)))

; ex3) 600851475143 의 가장 큰 소인수 구하기 

(def large-n 600851475143)
(defn first-factor [n]
  (loop [i 2]
    (cond
     (= n i) 1
     (= (mod n i) 0) i
     :else (recur (inc i)))))

(loop [n large-n 
       factor (first-factor large-n)]
  (if (= factor 1)
    n
    (let [q (/ n factor)] 
      (recur q
             (first-factor q)))))


; ex4 ) 세자리 수의 곱셈으로 구할 수 있는 가장 큰 대칭수 

(defn sym? [n]
  (let [cnt (count (str n))]
    (if (= (mod cnt 2) 1)
      false
      (let [str-n (str n)]
        (loop [start 0 end (dec cnt)]
          (cond
           (> start (dec end)) true
           (not= (get str-n start) (get str-n end)) false
           :else (recur (inc start) (dec end))))))))

(apply max (for [i (range 100 999)
           j (range 100 999)
           :let [n (* i j)]
           :when (sym? n)] n))

