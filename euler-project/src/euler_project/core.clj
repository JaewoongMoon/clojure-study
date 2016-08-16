(ns euler-project.core
  (:gen-class))

; ex1) 1000 보다 작은 자연수 중에서 3 또는 5의 배수를 모두 더하면?

(defn make-list
  [max-num]
  (loop [num 0 result []]
    (if (>= num max-num)
      result
      (recur (inc num) (conj result num)))))

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


(def large-n 600851475143)

(defn factor? [num candid]
  (= (mod num candid) 0))

;(defn prime? [num])

(factor? 100 10)

(defn factors-of [num]
  (set (map (fn [item] (if (factor? num item) item 1))
            (neutral))))

(defn search-factor [limit]
  (loop [start (dec limit)]
    (if (factor? limit start)
      (println (str "find:! " start))
      (recur (dec start)))))

(search-factor 199990)


(search-factor 1000)

(search-factor large-n)


(defn getFirstFactor [num]
  (loop [i 2]
    (if (= i num) 1)
    (if (= (mod num i) 0)
      i
      (recur (inc i)))))

(getFirstFactor 3)

(last
 (for [i (range 1000)
       j (range 1000)
       :let [n (* i j)]
       :when (and (= (mod n 13) 0)
                  (= (mod i 7) 0))]
   n))

(range 100 999)


(last
 (for [i (range 100 999)
       j (range 100 999)
       :let [n (* i j)]
       :when (sym? n)]
   n))




; Prime number
(defn get-primes [n]
  )


; [1 ... 20]

; 
(mod 20 5)


; 72 -> [{:mit 2 :zisu 3} {:mit 3 :zisu 2}]
; 72 를 2로 나눈다. 결과 36 나머지 0
; 나머지가 0이라면 계속 나눈다. 결과 18 나머지 0
; 나머지가 0이라면 계속 나눈다. 결과 9 나머지 0
; 2로 나눈 나머지가 0이 아니므로, 다음 소수인 3으로 나눈다. 
; 나머지가 0이라면 계속 나눈다. 결과 3 나머지 0
; 나머지 0이라면 계속 나눈다
