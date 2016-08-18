(ns euler-project.core
  (:gen-class))



; ----------------------------------------------------------------------------
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



; ----------------------------------------------------------------------------
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



; ----------------------------------------------------------------------------
; ex3) 600851475143 의 가장 큰 소인수 구하기 

(def large-n 600851475143)
(defn first-factor [n]
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

; ----------------------------------------------------------------------------
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


; ----------------------------------------------------------------------------
; ex5) 1~20 사이의 어떤 수로도 나누어 떨어지는 가장 작은 수 
; 최소 공배수 문제?
; 1) 1~20 까지의 수들을 소인수를 분해 한후
; 2) 소인수들 중에서 지수가 가장 큰 수를 찾아 서로 곱한다.
 

; STEP 1. 소인수 분해하는 함수 : 소인수 분해된 결과를 리턴한다.
; ex) 10을 소인수 분해하면 2^1 + 5^1 이다. 

; 1-1 ) prime-helper
; target (타겟 넘버), base (밑) 를입력받으면 target를 더 이상 나눠지지 않을 때까지 base로 나눈 결과를 리턴하는 함수
(defn prime-helper[target base]
  (loop [exponent 0 remain target]
    (if (not= (mod remain base) 0)
      {:base base :exponent exponent :remain remain}
      (recur (inc exponent)(/ remain base)))))

; 1-2) prime-helper2
; 인수의 exponent 가 0이면 제외시키고 0이 아니면 추가시키는 함수 

; 1-2) prime-factorization
; 2부터 시작해서 prime-helper에 넣는다.
; 결과 맵을 최종 결과 리스트에 저장한다.
; 결과 맵의 :remain 값이 0이면 종료
; 아닐경우 +1한 값, 3이 prime-helper에 들어간다.  (반복)

(defn prime-factorization[num]
  (loop [base 2 remain num result []]
;    (println result)
    (let [item (prime-helper remain base)]
      (if (= (get item :remain) 1)
        (conj result item)
        (recur (inc base)(get item :remain)(if (not= (get item :exponent) 0)
                                               (conj result item)
                                               result))))))



; STEP 2. 소인수 분해하는 함수에 1~20 까지의 리스트를 넣어서 소인수들로 구성된 리스트를 구한다.
(map prime-factorization (range 1 21))
(map prime-factorization [10 12])


; STEP 3.그 중에서 지수가 가장 큰 소인수들을 찾는다. 
; 지수가 가장 큰 소인수들로 구성된 리스트를 반환하는 함수
(defn )

(def my-test {:my ({:test 1}{:test 2}{:test 3})})

; STEP 4. 소인수들을 서로 곱한다.
; 결과 리스트를 계산하는 함수 : reduce 사용


 


