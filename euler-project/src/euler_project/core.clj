(ns euler-project.core
  (:gen-class))



; ----------------------------------------------------------------------------
; ex1) 1000 보다 작은 자연수 중에서 3 또는 5의 배수를 모두 더하면?

(defn is-multiple?
  ([num]
   (or (= (mod num 3) 0) (= (mod num 5) 0))))

(reduce + (set (map (fn [num] (if (is-multiple? num) num 0) ) 
                    (range 1 1000))))



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


; :exponent 의 set 으로 리턴하도록 개선한 방식 
; quotient : 몫
; exponent : 지수
; base : 밑 
(defn factorization-helper [num base]
  (loop [quotient num 
         exponent 0]
    (if (not= (mod quotient base) 0) ; 더 이상 나눠지지 않는다면 
      {:base base :exponent exponent :quotient quotient}
      (recur (/ quotient base)
             (inc exponent)))))

(defn prime-factorization[num]
  (loop [base 2 
         quotient num 
         result []]
    (let [item (factorization-helper quotient base)]
      (if (= (get item :quotient) 1) ;몫이 1, 즉 더 나눌 수 없다면
        (conj result item)
        (recur (inc base)
               (get item :quotient)
               (if (not= (get item :exponent) 0)
                 (conj result item)
                 result))))))

(prime-factorization 72)



(factor-cnt 500)

(get {:base 5, :exponent 3, :quotient 1} :exponent)

(prime-factorization 500)

; STEP 2. 소인수 분해하는 함수에 1~20 까지의 리스트를 넣어서 소인수들로 구성된 리스트를 구한다.
(map prime-factorization (range 1 21))
(map prime-factorization [10 12])

(def src (map prime-factorization [10 12]))
;=> ([{:base 2, :exponent #{1}, :quotient 5} {:base 5, :exponent #{1}, :quotient 1}] [{:base 2, :exponent #{2}, :quotient 3} {:base 3, :exponent #{1}, :quotient 1}])


; STEP 3. 소인수들의 리스트를 밑을 기준으로 정리한다. 
; 3-1. 소인수들의 지수목록을 정리해서 보여주는 함수
; 예를들어, 요런 모양으로 재편성 해주는 함수
; 결과 벡터에는 중복된 base 값이 없다. 
; => [{:base 2 :exponent #{1, 2, 3}}, {:base 3 :exponent #{2}]]




; STEP 4. 지수가 가장 큰 소인수들을 서로 곱한다.
; 제곱값 구하는 함수 
(defn exp [x n]
  (reduce * (repeat n x)))

(exp 2 3)

; 최종 결과 리스트를 계산하는 함수 : reduce 사용

; ver2.
(def test-src2 [{:base 2 :exponent #{1, 2, 3}} {:base 3 :exponent #{2}}])


(reduce * (map #(exp (get % :base) (apply max (get % :exponent))) 
               test-src2)) 

(apply max #{1 2 3})



;--
; 연습장..
; 결과 리스트를 순회한다.
(loop [remaining-list src 
       final-result []]
  (if (empty? remaining-list)
    final-result
    ()))

(empty? src)


; src4 를 순회하면서 
; 다음과 같은 리턴결과 만들기
; [{:base 2 :exponent #{1 2}} {:base 3 :exponent #{2}}]
(def src4 [{:base 2 :exponent #{1} }
           {:base 3 :exponent #{2} }
           {:base 2 :exponent #{2} }])

(loop [remaining src4 final-result []]
  (if (empty? remaining)
    final-result
    (let [[item & rest] remaining]
      (recur rest
             (if (get item ))                ; final-result 에 아이템 추가하기 
             ))))


; 추가할 때 로직이 final-result 에 해당 아이템이 있는지 없는지 체크를 하는 로직이 필요하다.
; 이를 위한 펑션 : has-base
(def result [{:base 2 :exponent 1}]) 
(def new-item {:base 2 :exponent 2}) 
(def new-item2 {:base 3 :exponent 3})

(defn has-base? [target base]
  (loop [remaining target]
    (if (empty? remaining)
      false
      (let [[item & rest] remaining]
        (if (= (get item :base) base)
          true
          (recur rest ))))))

(has-base? result 3)


(defn get-target-base-item [target base]
  (loop [remaining target]
    (if (empty? remaining)
      nil
      (let [[item & rest] remaining]
        (if (= (get item :base) base)
          item
          (recur rest ))))))

(get-target-base-item result 3)


; 하나의 소인수 분해 벡터값에 대해 재정렬 해주는 펑션 
(defn some-func [result vector]
  (loop [new-result result
         remaining vector]
    (let [[map & rest] remaining]
      (let [ (get-target-base-item result (get map :base)) target-map]
        (recur (if target-map
                 (do
                   (filter ) ; 결과 필터링 한 후에..
                   ; 값을 넣는다. 
                   (conj new-result (append-exponent target-map   
                                            (get map :exponent)))) 
                 (conj new-result {:base (get target-map :base)
                                   :exponent #{(get target-map :exponent)}}))
               )))))

; 1) 기존 맵에 새로운 지수 값을 추가하는 펑션 
; 예를들어, {:base 2 :exponent #{1}} 가 있을 때 2를 추가해서
; {:base 2 :exponent #{1 2}} 를 리턴한다.
(def map1 {:base 2 :exponent #{1} } )

(defn append-exponent [map exponent]
  {:base (get map :base)
    :exponent (conj (get map1 :exponent) exponent)})

(append-exponent map1 2)

; 2) 리스트에 새로운 맵을 추가 
(conj result new-item2) 


(set (#{1} #{2}))

(nth #{1} 0)

(nth [1] 0)





;-----------------------------------------------------------------------
; ex 6) 1부터 100까지 자연수에 대해 "합의 제곱"과 "제곱의 합"의 차이는 얼마입니까?
; 제곱의합.
(reduce + (map #(exp % 2) (range 1 11)))

;합의 제곱 
(exp (reduce + (range 1 11)) 2)

(- (exp (reduce + (range 1 101)) 2)
   (reduce + (map #(exp % 2) (range 1 101))))
 


;-----------------------------------------------------------------------------
; ex 7) 소수를 크기 순으로 나열하면 2, 3, 5, 7, 11, 13, ... 과 같이 됩니다.

; 이 때 10,001번째의 소수를 구하세요.

(def certainty 5)

(defn prime? [n]
  (.isProbablePrime (BigInteger/valueOf n) certainty))

(apply max (concat [2] (take 10000
                  (filter prime?
                          (take-nth 2
                                    (range 1 Integer/MAX_VALUE))))))

;------------------------------------------------------------------------------
; ex 8) 다음은 연속된 1000자리 숫자입니다 (읽기 좋게 50자리씩 잘라놓음).

(def ex8-num (clojure.string/replace   
"
73167176531330624919225119674426574742355349194934
96983520312774506326239578318016984801869478851843
85861560789112949495459501737958331952853208805511
12540698747158523863050715693290963295227443043557
66896648950445244523161731856403098711121722383113
62229893423380308135336276614282806444486645238749
30358907296290491560440772390713810515859307960866
70172427121883998797908792274921901699720888093776
65727333001053367881220235421809751254540594752243
52584907711670556013604839586446706324415722155397
53697817977846174064955149290862569321978468622482
83972241375657056057490261407972968652414535100474
82166370484403199890008895243450658541227588666881
16427171479924442928230863465674813919123162824586
17866458359124566529476545682848912883142607690042
24219022671055626321111109370544217506941658960408
07198403850962455444362981230987879927244284909188
84580156166097919133875499200524063689912560717606
05886116467109405077541002256983155200055935729725
71636269561882670428252483600823257530420752963450"
 "\n" ""))

; 5자리 스트링으로 split한  리스트로 만들기 
; 문제를 잘못이해했음. 5자리씩 끊는 것이 아니라, 연속된 5자리 스트링을 만들어야 함.  
(def string-list 
  (map (partial apply str) 
     (partition-all 5 ex8-num)))

(def string-list2
  (for [i (range (- (count ex8-num) 4))
        :let [item (subs ex8-num i (+ i 5))]]
    item))

; 리스트의 아이템(5자리 스트링)을 각각 Integer로 변환한 뒤 
; 각 숫자들을 곱한 값을 가지고 있는 리스트로 바꾸고, 최대값을 구한다. 
(apply max
       (map #(reduce *
                     (map (fn [^Character c] (Character/digit c 10)) %)) 
            string-list2))


;------------------------------------------------------------------------------
; ex 9) 세 자연수 a, b, c 가 피타고라스 정리 a^2 + b^2 = c^2 를 만족하면 피타고라스 수라고 부릅니다 (여기서 a < b < c ).
;예를 들면 3^2 + 4^2 = 9 + 16 = 25 = 5^2이므로 3, 4, 5는 피타고라스 수입니다.

;a + b + c = 1000 인 피타고라스 수 a, b, c는 한 가지 뿐입니다. 이 때, a × b × c 는 얼마입니까?

 (for [a (range 1 500)
       b (range 1 500)
       :let [c (- 1000 a b)]
       :when (and (= (+ (exp a 2) (exp b 2)) (exp c 2))
                  (and (< a b) (< b c))) ]
   (* a b c))

;------------------------------------------------------------------------------
; ex 10) 10 이하의 소수를 모두 더하면 2 + 3 + 5 + 7 = 17 이 됩니다.

;이백만(2,000,000) 이하 소수의 합은 얼마입니까?
(def certainty 5)

(defn prime? [n]
  (.isProbablePrime (BigInteger/valueOf n) certainty))

(println (reduce + (concat [2] 
                           (filter prime?
                                   (take-nth 2
                                             (range 1 2000000))))))


;-------------------------------------------------------------------------------
; ex 11) 아래와 같은 20×20 격자가 있습니다.
(def ex11-num "
08 02 22 97 38 15 00 40 00 75 04 05 07 78 52 12 50 77 91 08
49 49 99 40 17 81 18 57 60 87 17 40 98 43 69 48 04 56 62 00
81 49 31 73 55 79 14 29 93 71 40 67 53 88 30 03 49 13 36 65
52 70 95 23 04 60 11 42 69 24 68 56 01 32 56 71 37 02 36 91
22 31 16 71 51 67 63 89 41 92 36 54 22 40 40 28 66 33 13 80
24 47 32 60 99 03 45 02 44 75 33 53 78 36 84 20 35 17 12 50
32 98 81 28 64 23 67 10 26 38 40 67 59 54 70 66 18 38 64 70
67 26 20 68 02 62 12 20 95 63 94 39 63 08 40 91 66 49 94 21
24 55 58 05 66 73 99 26 97 17 78 78 96 83 14 88 34 89 63 72
21 36 23 09 75 00 76 44 20 45 35 14 00 61 33 97 34 31 33 95
78 17 53 28 22 75 31 67 15 94 03 80 04 62 16 14 09 53 56 92
16 39 05 42 96 35 31 47 55 58 88 24 00 17 54 24 36 29 85 57
86 56 00 48 35 71 89 07 05 44 44 37 44 60 21 58 51 54 17 58
19 80 81 68 05 94 47 69 28 73 92 13 86 52 17 77 04 89 55 40
04 52 08 83 97 35 99 16 07 97 57 32 16 26 26 79 33 27 98 66
88 36 68 87 57 62 20 72 03 46 33 67 46 55 12 32 63 93 53 69
04 42 16 73 38 25 39 11 24 94 72 18 08 46 29 32 40 62 76 36
20 69 36 41 72 30 23 88 34 62 99 69 82 67 59 85 74 04 36 16
20 73 35 29 78 31 90 01 74 31 49 71 48 86 81 16 23 57 05 54
01 70 54 71 83 51 54 69 16 92 33 48 61 43 52 01 89 19 67 48"
)
;위에서 대각선 방향으로 연속된 붉은 숫자 네 개의 곱은 26 × 63 × 78 × 14 = 1788696 입니다.
;그러면 수평, 수직, 또는 대각선 방향으로 연속된 숫자 네 개의 곱 중 최대값은 얼마입니까?
ex11-num




; 1) 20 x 20 좌표와 값의 데이터 구조로 만들어주는 함수
; ex) {loc:[0, 0] :value "08"} ...


; 2) 좌표에 해당되는 값을 가져오는 함수 


; 3) 수평, 수직, 대각선에 따라 연속된 숫자 네 개로 구성된 리스트를 구하는 함수

; 3-1) 수평 이동시 : row는 바뀌지 않고 column 값만 바뀌면서 찾으면 된다.(->) 모든row에 대해 
; 3-2) 수직 이동시 : column은 바뀌지 않고 row만 바뀌면서 찾으면된다. 모든 column에 대해)
; 3-3) 대각선 이동시: column 과 row가 시작점에서부터 1, 1 만큼 늘어나면서 찾으면 된다. 







;----------------------------------------------------------------------------
; ex 12) 1부터 n까지의 자연수를 차례로 더하여 구해진 값을 삼각수라고 합니다.
;예를 들어 7번째 삼각수는 1 + 2 + 3 + 4 + 5 + 6 + 7 = 28이 됩니다.
;이런 식으로 삼각수를 구해 나가면 다음과 같습니다.

;1, 3, 6, 10, 15, 21, 28, 36, 45, 55, ...
; 5개 이상의 약수를 갖는 첫번째 삼각수는 28입니다.
; 500개 이상의 약수를 갖는 가장 작은 삼각수는 얼마입니까?

; 삼각수 구하는 함수 
(defn tri-num[n]
  (reduce + (range 1 (+ n 1))))

(tri-num 7)

; 약수 리스트를 구하는 함수. 
(defn factor-list [n]
  (for [a (range 1 n)
        :when (= (mod n a) 0)]
    a))

(count (factor-list (tri-num 10))) ;=> 자연수 1부터 찾으므로 효율 안좋음. 

; 개선 : 약수 개수 카운트하는 함수 (소인수 분해 함수 이용)
(defn factor-cnt [n]
  (let [list (prime-factorization n)]
    (loop [[item & rest] list
           acc 1]
      (let [result (* acc (inc (get item :exponent)))]
        (if (empty? rest)
          result
          (recur rest
                 result))))))


; 삼각수 중에서 약수 카운트가 500이상인 수 찾기  
(loop [idx 1]
  (let [ num (tri-num idx)]
    (if (>= (factor-cnt num) 500)
      num
      (recur (inc idx)))))

;---------------------------------------------------------------------------
; ex 13) 아래에 50자리 숫자가 100개 있습니다. 이것을 모두 더한 값의 첫 10자리는 얼마입니까?
(def ex13-num "37107287533902102798797998220837590246510135740250
46376937677490009712648124896970078050417018260538
74324986199524741059474233309513058123726617309629
91942213363574161572522430563301811072406154908250
23067588207539346171171980310421047513778063246676
89261670696623633820136378418383684178734361726757
28112879812849979408065481931592621691275889832738
44274228917432520321923589422876796487670272189318
47451445736001306439091167216856844588711603153276
70386486105843025439939619828917593665686757934951
62176457141856560629502157223196586755079324193331
64906352462741904929101432445813822663347944758178
92575867718337217661963751590579239728245598838407
58203565325359399008402633568948830189458628227828
80181199384826282014278194139940567587151170094390
35398664372827112653829987240784473053190104293586
86515506006295864861532075273371959191420517255829
71693888707715466499115593487603532921714970056938
54370070576826684624621495650076471787294438377604
53282654108756828443191190634694037855217779295145
36123272525000296071075082563815656710885258350721
45876576172410976447339110607218265236877223636045
17423706905851860660448207621209813287860733969412
81142660418086830619328460811191061556940512689692
51934325451728388641918047049293215058642563049483
62467221648435076201727918039944693004732956340691
15732444386908125794514089057706229429197107928209
55037687525678773091862540744969844508330393682126
18336384825330154686196124348767681297534375946515
80386287592878490201521685554828717201219257766954
78182833757993103614740356856449095527097864797581
16726320100436897842553539920931837441497806860984
48403098129077791799088218795327364475675590848030
87086987551392711854517078544161852424320693150332
59959406895756536782107074926966537676326235447210
69793950679652694742597709739166693763042633987085
41052684708299085211399427365734116182760315001271
65378607361501080857009149939512557028198746004375
35829035317434717326932123578154982629742552737307
94953759765105305946966067683156574377167401875275
88902802571733229619176668713819931811048770190271
25267680276078003013678680992525463401061632866526
36270218540497705585629946580636237993140746255962
24074486908231174977792365466257246923322810917141
91430288197103288597806669760892938638285025333403
34413065578016127815921815005561868836468420090470
23053081172816430487623791969842487255036638784583
11487696932154902810424020138335124462181441773470
63783299490636259666498587618221225225512486764533
67720186971698544312419572409913959008952310058822
95548255300263520781532296796249481641953868218774
76085327132285723110424803456124867697064507995236
37774242535411291684276865538926205024910326572967
23701913275725675285653248258265463092207058596522
29798860272258331913126375147341994889534765745501
18495701454879288984856827726077713721403798879715
38298203783031473527721580348144513491373226651381
34829543829199918180278916522431027392251122869539
40957953066405232632538044100059654939159879593635
29746152185502371307642255121183693803580388584903
41698116222072977186158236678424689157993532961922
62467957194401269043877107275048102390895523597457
23189706772547915061505504953922979530901129967519
86188088225875314529584099251203829009407770775672
11306739708304724483816533873502340845647058077308
82959174767140363198008187129011875491310547126581
97623331044818386269515456334926366572897563400500
42846280183517070527831839425882145521227251250327
55121603546981200581762165212827652751691296897789
32238195734329339946437501907836945765883352399886
75506164965184775180738168837861091527357929701337
62177842752192623401942399639168044983993173312731
32924185707147349566916674687634660915035914677504
99518671430235219628894890102423325116913619626622
73267460800591547471830798392868535206946944540724
76841822524674417161514036427982273348055556214818
97142617910342598647204516893989422179826088076852
87783646182799346313767754307809363333018982642090
10848802521674670883215120185883543223812876952786
71329612474782464538636993009049310363619763878039
62184073572399794223406235393808339651327408011116
66627891981488087797941876876144230030984490851411
60661826293682836764744779239180335110989069790714
85786944089552990653640447425576083659976645795096
66024396409905389607120198219976047599490197230297
64913982680032973156037120041377903785566085089252
16730939319872750275468906903707539413042652315011
94809377245048795150954100921645863754710598436791
78639167021187492431995700641917969777599028300699
15368713711936614952811305876380278410754449733078
40789923115535562561142322423255033685442488917353
44889911501440648020369068063960672322193204149535
41503128880339536053299340368006977710650566631954
81234880673210146739058568557934581403627822703280
82616570773948327592232845941706525094512325230608
22918802058777319719839450180888072429661980811197
77158542502016545090413245809786882778948721859617
72107838435069186155435662884062257473692284509516
20849603980134001723930671666823555245252804609722
53503534226472524250874054075591789781264330331690")


; \n로 분리한 문자열 리스트 만들기
(def string-list
  (clojure.string/split ex13-num #"\n"))


; 각 숫자들을 Number 값으로 변환한 후 
; 더하고 앞의 10자리 값을 얻어온다. 
(subs (str(reduce +
        (map read-string string-list))) 
 0 10)





; ex14) 양의 정수 n에 대하여, 다음과 같은 계산 과정을 반복하기로 합니다.

;n → n / 2 (n이 짝수일 때)
;n → 3 n + 1 (n이 홀수일 때)

;13에 대하여 위의 규칙을 적용해보면 아래처럼 10번의 과정을 통해 1이 됩니다.

;13 → 40 → 20 → 10 → 5 → 16 → 8 → 4 → 2 → 1
;아직 증명은 되지 않았지만, 이런 과정을 거치면 어떤 수로 시작해도 마지막에는 1로 끝나리라 생각됩니다. 
;(역주: 이것은 콜라츠 추측 Collatz Conjecture이라고 하며, 이런 수들을 우박수 hailstone sequence라 부르기도 합니다)

;그러면, 백만(1,000,000) 이하의 수로 시작했을 때 1까지 도달하는데 가장 긴 과정을 거치는 숫자는 얼마입니까?

(defn calc-num [n]
  (if (=(mod n 2) 0)
  (/ n 2)
  (+ (* 3 n) 1)))

(calc-num 10)

(defn check-cnt [n]

)


; ex20) n! 이라는 표기법은 n × (n − 1) × ... × 3 × 2 × 1을 뜻합니다.

;예를 들자면 10! = 10 × 9 × ... × 3 × 2 × 1 = 3628800 이 되는데,
;여기서 10!의 각 자리수를 더해 보면 3 + 6 + 2 + 8 + 8 + 0 + 0 = 27 입니다.

;100! 의 자리수를 모두 더하면 얼마입니까?

(defn factorial [n]
  (loop [i n acc 1]
     (if (= i 1)
       acc
       (recur (dec i)
               (*' acc i)))))

(defn split-num [n]
  (clojure.string/split (str n) #""))

(defn sum-each-numbers [n]
   (reduce + (map read-string (split-num n))))


