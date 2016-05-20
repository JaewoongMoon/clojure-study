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
(defn neutral-numbers [start]
  (lazy-seq 
   (cons start 
         (neutral-numbers (inc start) ))))

(def neutral (take 600851475143 (neutral-numbers 1)))

(second neutral)

(defn is-factor? [num candid]
  (= (mod num candid) 0))

(is-factor? 100 5)

(defn factors-of [num]
  (set (map (fn [item] (if (is-factor? num item) item 1))
            (neutral))))

; out of memory error
;(factors-of 600851475143)

(defn factors-of2 [limit]
  (loop [iter 1 result []]
;    (println (str "result :" result))
    (if (> iter limit)
      result
      (recur (inc iter) 
             (if (is-factor? limit iter)
               (conj result iter) result)))))

(factors-of2 600851475143)


(loop [iter 1]
  (println (str "Iter:" iter))
  (if (> iter 1000)
    (println "Goodbye!")
    (recur (inc iter))))


(+ 1 2)
