(ns ch4-core-functions-in-depth.core
  (:gen-class))

; ***************************************************************************
; Chapter 4. CORE FUNCTIONS IN DEPTH

;; PROGRAMMING TO ABSTRACTIONS    
;; -> sequence and collection abstractiions.
; To understand programming to abstractions, let's compare Clojure to a language that wasn't built with that principle in mind : Emacs Lisp(elisp). In elisp, you can use the mapcar function to derive a new list, which is similar to how you use map in Clojure. However, if you want to map over a hash map (similar to Clojure's map data structure) in elisp, you'll need to use the maphash function, whereas in Clojure you can still just use map. In other words, elisp uses two different, data structure-specific functions to implement the map operation, but Clojure uses only one. You can also call reduce on a map in Clojure, whereas elisp doesn't provide a function for reducing a hash map. 
; The reason is that Clojure defines map and reduce functions in terms of the sequence abstraciton, not in terms of specific data structures. 
; Lists, vectors, sets, and maps all implement the sequence abstraction, so they all work with amp, as shown here,

(defn titleize
  [topic]
  (str topic " for the Brave and True"))

(titleize "Potter")
; -> Pooter for the Brave and True

; for vectors
(map titleize ["Hamsters" "Ragnarok"])

; for lists
(map titleize '("Emphaty" "Decorating"))

; for (unsorted) sets
(map titleize #{"Elbows" "Soap Carving"})

; for anonymous functions 
(map #(titleize (second %)) {:uncomfortable-thing "Winking"})


;; ABSTRACTION THROUGH INDIRECTION
; At this point, you might object that I'm just kicking the can down the road 
; because we're still left with the problem of how a function like first is able to work wirth different data structures. 
; Clojure does this using two forms of indirection, In programming, indirection is a generic term for the mechanisms a language employs so that one name can have multiple, related meanings.
; Indirection is what makes abstraction possible.

; Polymorphism is one way that Clojure provides indirection. 
; Basically, polymorphic functions dispatch to different function bodies based on the type of the argument supplied.

; Note Clojure has two constructs for defining polymorphic dispatch: 
; 1) the host platform's interface construct and 
; 2) platform-independent protocols.
; But it's not necessary to understand how these work 
; when you're just getting started. I'll cover protocols in Chapter 13.



(seq '(1 2 3)) 

(map inc [1 2 3])

(map str ["a" "b" "c" ] ["A" "B" "C"])

(list (str "a" "A") (str "b" "B") (str "c" "C"))

(def human-consumption   [8.1 7.3 6.6 5.0])
(def critter-consumption [0.0 0.2 0.3 1.1])
(defn unify-diet-data
  [human critter]
  {:human human
   :critter critter})

(map unify-diet-data human-consumption critter-consumption)

(def sum #(reduce + %))

(def avg #(/ (sum %) (count %)))

(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))


(stats [3 4 10])

(stats [80 1 44 13 6])

(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spider-Man" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}])

(map :real identities)

; default usage of reduce : 
(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 10})

; Another usage of reduce : a filter
(reduce (fn [new-map [key val]]
          (if (> val 4)
            (assoc new-map key val)
            new-map))
        {}
        {:human 4.1
         :critter 3.9})

(take 3 [1 2 3 4 5 6 7 8 9 10])

(drop 3 [1 2 3 4 5 6 7 8 9 10])



(def food-journal
  [{:month 1 :day 1 :human 5.3 :critter 2.3}
   {:month 1 :day 2 :human 5.1 :critter 2.0}
   {:month 2 :day 1 :human 4.9 :critter 2.1}
   {:month 2 :day 2 :human 5.0 :critter 2.5}
   {:month 3 :day 1 :human 4.2 :critter 3.3}
   {:month 3 :day 2 :human 4.0 :critter 3.8}
   {:month 4 :day 1 :human 3.7 :cirrter 3.9}
   {:month 4 :day 2 :human 3.7 :critter 3.6}])

(drop-while #(< (:month %) 3) food-journal)

(take-while #(< (:month %) 4) 
            (drop-while #(< (:month %) 2) food-journal))

(filter #(< (:human %) 5) food-journal)

(some #(and (> (:critter %) 3) %) food-journal)

(sort [3 1 2])

(sort-by count ["aaa" "c" "bb"])

(concat [1 2] [3 4])

(def vampire-database
  {0 {:makes-blood-puns? false, :has-pulse? true :name "McFishwich"}
   1 {:makes-blood-puns? false, :has-pulse? true :name "McMackson"}
   2 {:makes-blood-puns? true,  :has-pulse? false :name "Damon Salvatore"}
   3 {:amkes-blood-puns? true, :has-pulse? true :name "Mickey Mouse"}})

(defn vampire-related-details
  [social-security-number]
  (Thread/sleep 1000)
  (get vampire-database social-security-number))

(defn vampire?
  [record]
  (and (:makes-blood-puns? record)
       (not (:has-pulse? record))
       record))

(defn identify-vampire
  [social-security-numbers]
  (first (filter vampire?
                 (map vampire-related-details social-security-numbers))))

(time (vampire-related-details 0))

; example of lazy seq 
; range returns a lazy sequence consisting of the intergers from 0 to 999,999
(time (def mapped-details (map vampire-related-details (range 0 1000000))))
; You can think of a lazy seq as consisting of two parts;
; a recipe for how to realize the elements of a sequence 
; and the elements that have been realized so far.


(time (first mapped-details))


(time (identify-vampire (range 0 1000000)))

;one cool, useful capability that lazy seqs give you is the ability to construct infinite sequences. So far, you've only worked with lazy sequences generated from vectors or listat that terminated.
; However, Clojure comes with a few functions to create infinate sequences.
; One easy way to create an infinate sequence is with repeat, which creates a sequence whose every member is the argument you pass:
(concat (take 8 (repeat "na")) ["Batman!"]) 

(take 3 (repeatedly (fn [] (rand-int 10))))

; A lazy seq's recipe doesn't have to specify an endpoint. 
; Functions like first and take, which realize the lazy seq,
; have no way of knowing what will come next in a seq,
; and if the seq keeps providing elements, well, they'll just keep taking them. 
(defn even-numbers
  ([] (even-numbers 0))
  ([n] (cons n (lazy-seq (even-numbers (+ n 2))))))

(take 10 (even-numbers))

;; THE COLLECTION ABSTRACTION
; The sequence abstraction is about operating on members individually,
; whereas the collection abstraction is about the data structure as as whole.

; One of the most important collection functions is into.
; info function conver the return value back into the original value.
;ex1)
(map identity {:sunlight-reaction "Glitter!"})

(into {} (map identity {:sunlight-reaction "Glitter!"}))
; the map function returns a sequential data structure(list) after being given a map data structure, and into converts the seq back into a map.

; ex2) This will work with other data structures as well:
(map identity [:garlic :sesame-oil :fried-eggs])

(into [] (map identity [:garlic :sesame-oil :fried-eggs]))

; ex3) 
(map identity [:garlic-clove :garlic-clove])

(into #{} (map identity [:garlic-clove :garlic-clove]))

; The first element of into doesn't have to be empty.
(into ["cherry"] '("pine" "spruce"))

;; FUNCTION FUNCTIONS

;;; apply
(max 0 1 2)

(max [0 1 2])
; -> doesn't work 

(apply max [0 1 2])
;->2 

;;; partial 
; partial takes a function and any number of arguments.
; It then returns a new function.
; When you call the returned function, 
; it calls the original function with the original arguments you supplied it along with the new arguments.

(def add10 (partial + 10))
(add10 3)

(def add-missing-elements
  (partial conj ["water" "earth" "air"]))

(add-missing-elements "unobtainium" "adamantium")

(defn my-partial 
  [partialized-fn & args]
  (fn [& more-args]
    (apply partialized-fn (into args more-args))))

(def add20 (my-partial + 20))
(add20 3)

; In general, you want to use partials 
; when you find you're repeating the same combination of function and arguments in mary different contexts.

; This toy example shows how you could use partial to specialize a logger,creating a warn function. 

(defn lousy-logger
  [log-level message]
  (condp = log-level
    :warn (clojure.string/lower-case message)
    :emergency (clojure.string/upper-case message)))

(def warn (partial lousy-logger :warn))

(warn "Red light ahead")

(def emergency (partial lousy-logger :emergency))

(emergency "holly shit! It's emergency!")

;;; COMPLEMENT

(defn identify-humans
  [social-security-numbers]
  (filter #(not (vampire? %))
          (map vampire-related-details social-security-numbers)))

(def not-vampire? (complement vampire?))

(defn identify-humans
  [social-security-numbers]
  (filter not-vampire?
          (map vampire-related-details social-security-numbers)))

(identify-humans [0 1 2 3])

(neg? -1)

(complement (neg? 1))

(defn my-complement
  [fun]
  (fn [& args]
    (not (apply fun args))))

(def my-pos? (complement neg?))
(my-pos? 1)
(my-pos? -1)


;To pull everything together, let's write the beginnings of a sophisticated vampire data analysis program for the Forks, Washington Police Departement(FWPD).


(defn sum
  ([vals]
   (sum vals 0))
  ([vals accumulating-total]
   (if (empty? vals)
     accumulating-total
     (sum (rest vals) (+ (first vals) accumulating-total)))))

(sum [1 2 3])

((comp inc *) 2 3)

(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
                :st}})



(defn random-ints [limit]
  "Returns a lazy seq of random integers in the range [0, limit]"
  (lazy-seq 
   (println "realizing random number")
   (cons (rand-int limit)
          (random-ints limit))))


(def rands (take 10 (random-ints 50)))

(first rands)

(second rands)

(nth rands 3)

(count rands)

