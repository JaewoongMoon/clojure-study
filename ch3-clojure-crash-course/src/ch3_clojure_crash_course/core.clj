(ns ch3-clojure-crash-course.core
  (:gen-class))

;************************************************************************
; Chapter 3. Do Things : A Clojure Crash Course
(str "It was the panda " "in the library" "with a dust buster")

;; CONTROL FLOW 
(if true
  (do (println "Success!")
      "By Zeus's hammer!")
  (do (println "Failure!")
      "By Aquaman's trident!"))

;;; nil, true, false, Truthniss, Equality, and Boolean Expressions 
(or false nil :large_I_mean_venti :why_cant_I_just_say_large)

; nil is incicated no value in Clojure
(nil? 1) 
;-> false

(nil? nil) 
;->true



(or (= 0 1) (= "yes" "no"))

;; NAMING VALUES WITH DEF
(def failed-protagonist-names
  ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"])

failed-protagonist-names

(defn error-message
  [severity]
  (str "OH GOD! IT'S A DISASTER! WE'RE "
       (if (= severity :mild)
         "MILDY INCONVENIENCED!"
         "DOOOOOOOOOOOMED!")))


; DATA STRUCTURES
;; MAPS
{}
{"string-key" +}

(hash-map :a 1 :b 2)

; I can look uy values in maps with the get function
(def test-map {:a 0 :b 1})
 
(get test-map :b)
; -> 1

(get {:a 0 :b {:c "ho hum"}} :b)
; -> {:c "ho hum"}

(get {:a 0 :b {:c "ho hum"}} :c)
; -> nil

; give the default value
(get {:a 0 :b 1} :c "unicorns?")
; -> "unicorns?" 

; look up in nested map
(get-in {:a 0 :b {:c "ho hum"}} [:b :c])

; Another way to look up a value in a map
; Treat the map like a function with the key as its argument
({:name "The Human Coffeepot"} :name)
; -> "The Human Coffepot"



;; KEYWORDS
; Keywords can be used as functions
; that look up the coreesponding value in a data structure
; ex) look up :a in a map
(:a {:a 1 :b 1 :c 3})
; -> 1
; is equivalent to 
(get {:a 1 :b 2 :c 3} :a)

; with a default value 
(:d {:a 1 :b 2 :c 3} "No gnome knows like Noah knows")



;; VECTORS
; A vector is similar to an array, in that it's a 0-indexed collection.

; Returning the 0th element of a vector
(get [3 2 1] 0)

; Another example of getting by index
(get ["a" {:name "Pulsley Winterbottom"} "c"] 1)
; -> {:name "Pulsley Winterbottom}

; I can create vectors with the vector function
(vector "creepy" "full" "moon")
;-> ["creepy" "full" "moon"]

; I can use the conj function to add additional elements to the vector.
(conj [1 2 3] 4)
; -> [1 2 3 4]



;; LISTS
; Lists are similar to vectors in that they're linear collections of values
; But there are some differences. 
; I can't retrieve list elements with get.
 
'(1 2 3 4)
; -> (1 2 3 4)

; For retrieve an element from a list, I can use nth function.
(nth '(:a :b :c) 2)
; -> :c
; nth is slower than get 

; List values can have any type, and I can create lists with the list function.
(list 1 "two" {3 4})

; Elements are added to the beginning of a list (Not like a vector)
(conj '(1 2 3)4)
;-> (4 1 2 3)




;; SETS
; Sets are collections of unique values.
; Clojure has two kinds of sets: hash sets and sorted sets.
; I'll focus on hash sets because they're used more often. 
#{"kurt vonnegut" 20 :icicle}


; Another way to make hash set
(hash-set 1 1 2 2)
; -> #{1 2}

(conj #{:a :b} :b)
; -> #{:a :b}

(set [3 3 3 4 4])
; -> #{3 4}

; check for set membership
(contains? #{:a :b} :a)
; -> true

(contains? #{:a :b} 3)
; -> false

(contains? #{nil} nil)
; -> true

; get element from hash set by using a key word
(:a #{:a :b})
; -> :a

(get #{:a :b} :a)
; -> :a

(get #{:a nil} nil)
; -> nil

(get #{:a :b} "kurts")
; -> nil



; FUNCTIONS
;; CALLING FUNCTIONS

; returns the + function 
; because return value of OR is the first truthy value
(or + -)

((or + -) 1 2 3)
; -> 6

; return value of AND is the first falsey value or the last truthy value
((and (= 1 1) +) 1 2 3)
; -> 6

((first [+ 0]) 1 2 3)
; -> 6

(inc 1.1)

; map creates a new list by applying a function to each member of a collection.
(map inc [0 1 2 3])
-> (1 2 3 4)
; Note that map doesn't return a vector, even though we supplied a vector as an argument. 



;; MACRO CALLS, AND SPECIAL FORMS
; special forms example : def, if ...
; 1) special forms, unlike function calls, they  don't always evaluate all of their operands.
; 2) special forms can not be used as an argument. 

; macros are similar to special forms in that they evaluate their operands differently from function calls, and they also can't be passed as arguments to functions. 

;; DEFINING FUNCTIONS

(defn x-chop
  "Describe the kind of chop you're inflicting on someone"
  ([name chop-type]
   (str "I " chop-type " chop " name "! Take that!"))
  ([name]
   (x-chop name "karate"))
  )

(x-chop "Kanye West" "slap")


(defn codger-communication
  [whippersnapper]
  (str "Get off my lawn, " whippersnapper "!!!"))


; Clojure also allows you to define variable-arity functions by including a 
; rest parameter, as in "put the rest of these arguments in a list
; with the following name."
; The rest parameter is indicated by an ampersand (&). 

(defn codger
  [& whippersnappers]
  (map codger-communication whippersnappers))

(codger "Billy" "Anne-Marie" "The Incredible Bulk")

; You can mix rest parameters with normal parameters, 
; but the res parameter has to come last. 
(defn favorate-things
  [name & things]
  (str "Hi, " name ", here are my favorate things: "
       (clojure.string/join ", " things)))

(favorate-things "sexy" "Doreen" "gum" "shoes" "kara-te")


; Clojure has a more sophisticated way of defining parameters,
; called destructuring, which deserves its own subsection. 
; The basic idea behind destructuring is that it lets you concisely bind names
; to values within a collection. 

;; Return the first element of a collection
(defn my-first
  [[first-thing]] ; Notice that first-thing is within a vector
  first-thing)

(my-first ["oven" "bike" "war-axe"])
; -> "oven" 
; That vector is like a huge sign held up to Clojure that says,
; "Hey! This function is going to receive a list or a vector as an argument.
; Make my life easier by taking apart the argument's structure for me
; and associating meaningful names with differt parts of the argument!"



; When destructuring a vector or a list , 
; you can name as many elements as you want and also use rest parameters

; What the hell is happen?




(defn chooser
  [[first-choice second-choice & unimportant-choices]]
  (println (str "Your first choice is : " first-choice))
  (println (str "Your second choice is : " second-choice))
  (println (str "We're ignoring the rest of your choices. "
                "Here they are in case you need to cry over them: "
                (clojure.string/join ", " unimportant-choices ))))

(chooser ["Marmalade", "Handsome Jack", "Pigpen", "Aquaman"])

(defn announce-treasure-location
 ; [{lat :lat lng :lng}]
  [{:keys [lat lng]}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))

(announce-treasure-location {:lat 28.22 :lng 81.33})



;; FUNCTION BODY

(defn illustrative-function
  []
  (+ 1 304)
  30
  "joe")

(illustrative-function)

(defn number-comment
  [x]
  (if (> x 6)
    "Oh my gosh! What a big number!"
    "That number's OK, I guess"))

(number-comment 5)

(number-comment 7)

(map (fn [name] (str "Hi, " name))
     ["Darth Vader" "Mr. Magoo"])

; ****************************************************************
; **************  ANONYMOUS FUNCTIONS ***************************
((fn [x] (* x 3)) 8)

(def my-special-multiplier (fn [x] (* x 3)))

(my-special-multiplier 12)

; Another way of creating anonymous function ; So wired! huh?

(#(* % 3) 8)
; -> 24

; Here's an example of passing an anonymous function as an argument to map: 

(map #(str "Hi, " %)
     ["Darth Vader" "Mr. Magoo"])
; -> ("Hi, Darth Vader" "Hi, Mr. Magoo")

; This strange-looking style of writing anonymous functions is made possible by a feature called READER MACROS. You'll learn all about those in Chapter 7.
; Right now, it's okay to learn how to use just these anonymous functions. 
; You can see that this syntax is definitedly more compact, but it's also a little odd. Let's break it down. This kind of anonymous function looks a lot like a function call, except that it's preceded by a hash mark, #:

;; Function call
(* 8 3)

;; Anonymous funciton 
#(* % 3)

; This similarity allows you to more quickly see what will happen when this anonymous function is applied. "Oh," you can say to yourself, "this is going to multiply its argument by three."
; As you may have guessed by now, the percent sign,%, indicates the argument passed to the function. If your anonymous function takes multiple arguments, you can distinguish them like this: %1, %2, %3, and so on. % is equivalent to %1 :

(#(str %1 " and " %2) "cornbread" "butter beans")
; -> cornbread and butter beans

; You can also pass a rest parameter with %& : 
(#(identity %&) 1 "blarg" :yip)
; -> (1 "blarg" :yip)

; In this case, you applied the identity function to the rest argument. Identity returns the argument it's given without altering it. Rest arguments are stored as lists, so the function application return a list of all the arguments.
; If you need to write a simple anonymous function, using this style is best because it's visually compact. On the other hand, it can easily become unreadable if you're writing a longer, more complex function. If that's the case, use fn. 



;*************************************************************************
;; RETURNING FUNCTIONS

; By now you've seen that functions can return other functions. The returned functions are closures, which means that they can access all the variables that were in scope when the function was created. Here's a standard example :
 
(defn inc-maker
  "Create a custom incrementor"
  [inc-by]
  #(+ % inc-by))


(def inc3 (inc-maker 3))

(inc3 7)
; => 10 

; Here, inc-by is in scope, so the returned function as access to it even when the returned function is used outside inc-maker. 



; ******************************************************************
; PULLING IT ALL TOGETHER
; Okay! It's time to use your newfound knowledge for a noble purpose: smacking around hobbits! 

;; THE SHIRE's NEXT TOP MODEL 
; For our hobbit model, we'll eschew such hobbit characteristics as joviality and mischievousness and focus only on the hobbit's tiny body. Here's the hobbit model. 
(def asym-hobbit-body-part [{:name "head" :size 3}
                            {:name "left-eye" :size 1}
                            {:name "left-ear" :size 1}
                            {:name "mouth" :size 1}
                            {:name "nose" :size 1}
                            {:name "neck" :size 2}
                            {:name "left-shoulder" :size 3}
                            {:name "left-upper-arm" :size 3}
                            {:name "chest" :size 10}
                            {:name "back" :size 10}
                            {:name "left-forearm" :size 3}
                            {:name "abdomen" :size 6}
                            {:name "left-kidney" :size 1}
                            {:name "left-hand" :size 2}
                            {:name "left-knee" :size 2}
                            {:name "left-thigh" :size 4}
                            {:name "left-lower-leg" :size 3}
                            {:name "left-achilles" :size 1}
                            {:name "left-foot" :size 2}])

; This is a vector of maps. Each map has the name of the body part and relative size of the body part. (I know that only anime characters have eyes one-third the size of their head, but just go with it, okay?)


(defn matching-part
  [part]
  {:name (clojure.string/replace (:name part) #"^left-" "right-")
   :size (:size part)})

(matching-part {:name "left-hand" :size 8})

(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size "
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
               (into final-body-parts
                     (set [part (matching-part part)])))))))


(symmetrize-body-parts asym-hobbit-body-part)

; The symmetrize-body-parts function employs a general strategy that is common in functional programming. 
; Given a sequence (in this case, a vector of body parts and their sizes), 
; the function continuously splits the sequence into a head and a tail.
; Then it processes the head, adds it to some result, 
; and uses recursion to continue the process with the tail. 

; The tail of the sequence will be bound to remaining-asym-part.
; Initially, it's bound to the full sequence passes to the function: asym-body-parts 
; We also create a result sequence, final-body-parts; its initial value is an empty vector. 

; We resur with remaining, a list that gets shorter by on element on each iteration of the loop, and the (into) expression, which builds our vector of symmetrized body parts.

; DETAIL EXPLAIN OF ABOVE START ********************************************
; 1) let  
; bind the name x to the value 3.
(let [x 3] x)


; let also introduces a new scope
; , which means local variables
(def x 0)
(let [x 1] x)
; -> 1
x
; -> 0

(def x 0)
(let [x (inc x)] x)
; -> 1


(def dalmatian-list
  ["Pongo" "Perdita" "Puppy 1" "Puppy 2"])

; bind the name dalmatians to the result of the expression (take 2 dalmatian-list)
(let [dalmatians (take 2 dalmatian-list)]
  dalmatians)
; -> ("Pongo" "Perdita")

; you can also use rest parameters in let, just like you can in funaction : 
(let [[pongo & dalmatians] dalmatian-list]
  [pongo dalmatians])
; -> ["Pongo" ("Perdita" "Puppy 1" "Puppy2")]

; Notice that the value of a let form is the last form in its body that is evaluated. let forms follow all the destructing rules introduced in "Calling Functions". In this case, [pongo & dalmatians] destructed dalmatian-list, binding the string "Pongo" to the name pongo and the list of the rest of the dalmatians to dalmatians. The vector [pongo dalmatians] is the last expression in let, so it's the value of the let form. 

; let forms have two main uses. First, they provide clarity by allowing you to name things. Second, they allow you to evaluate an expression only once and reuse the result. This is especially important when you need to reuse the result of an expensive function call, like a network API call. It's also important when the expression has side effects. 

; 2) loop
(loop [iteration 0]
  (println (str "Iteration " iteration))
  (if (> iteration 3)
    (println "Goodbye!")
    (recur (inc iteration))))


; 3) Regular Expressions
; Regular expressions are tools for performing pattern maching on text.
; literal notation : to place the expression in quotes after a hash mark.
; #"regular-expression"
 
(re-find #"^left-" "left-eye")
; -> "left-"

(re-find #"^left-" "cleft-eye")
; -> nil
(re-find #"^left-" "aweirjliajweig")
; -> nil 

(matching-part {:name "left-eye" :size 1})
; -> {:name "right-eye" :size 1}

(matching-part {:name "head" :size 3})
; -> {:name "head" :size 3}

; DETAIL EXPLAIN END ******************************************************


;; BETTER SYMMETRIZER WITH REDUCE
; The pattern of process each element in a sequence and build a result is so common that thre's a built-in function for it called reduce.
; ex)
(reduce + [1 2 3 4])
; -> 10 
; it's same as (+ (+ (+ 1 2) 3) 4)

; The reduce function works according to the following steps:
; 1. Apply the given function to the first two elements of a sequence.
; 2. Apply the given function to the result and the next element of the sequence.
; 3. Repeat step 2 for every remaining element in the sequence. 

; reduce also takes an optional initial value. 
; ex)
(reduce + 15 [1 2 3 4])
; -> 25 
; If you provide an inital value, reduce starts by applying the given function to the initial value and the first element of the sequence rather than the first two elements of the sequence.
; One detail to note is that, in these examples, reduce takes a collection of elements, [1 2 3 4], and returns a single number. Although programmers often use reduce this way, you can also use reduce to treturn an even larger collection than the one you started with, as we're trying to do with symmetrize-body-parts.
; reduce abstracts the task "process a collection and build a result,"
; which is agnostic about the type of result returned.
; ex)
(defn my-reduce
  ([f initial coll]
   (loop [result initial
          remaining coll]
     (if (empty? remaining)
       result
       (recur (f result (first remaining)) (rest remaining)))))
  ([f [head & tail]]
   (my-reduce f head tail)))



(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size "
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
               (into final-body-parts
                     (set [part (matching-part part)])))))))



;************************************************************************

(defn better-symmetrize-body-parts
  [asym-body-parts]
  (reduce (fn [final-body-parts part]
            (into final-body-parts (set [part (matching-part part)])))
          []
          asym-body-parts))

(reduce + [] [1 2 3])

;; HOBBIT VIOLENCE
(defn hit
  [asym-body-parts]
  (let [sym-parts (better-symmetrize-body-parts asym-body-parts)
          body-part-size-sum (reduce + (map :size sym-parts))
          target (rand body-part-size-sum)]
      (loop [[part & remaining] sym-parts
             accumulated-size (:size part)]
        (if (> accumulated-size target)
          part
          (recur remaining (+ accumulated-size (:size (first remaining))))))))

(hit asym-hobbit-body-part)

(def sym-body-parts (better-symmetrize-body-parts asym-hobbit-body-part))

(reduce + (map :size (sym-body-parts)))

; hit works by taking a vector of asymmetrical body parts, symmetrizing it 
; and then summing the sizes of the parts
; Once we sum the sizes, it's like each number from 1 through body-part-size-sum corresponds to a body part;
; 1 might correspond to the left eye, and 2, 3, 4 might coreespond to the head.
; This makes it so when you hit a body part (by choosing a random number in this range), the likelihood that a particular body part is hit will depend on the size of the body part.
; Finally, one of these numbers is randomly chosen, and then we use loop to find and return the body part that coreesponds to the number. 
; The loop does this by keeping track of the accumulated sizes of parts that we've checked and checking whether the accumulated size is greater than the target.
; I visualize this process as lining up the body parts with a row of numbered slots.
; After I line up a body part, I ask myself, "Have I reached the target yer?"
; If I have, that means the body part I just lined up was the on hit.
; Othersize, I just keep lining up those parts.

; EXERCISES
; ex2) Write a function that takes a number and adds 100 to it.
(defn addFrom100
  [number]
  (loop [iteration 100 result 0]
    (println (str "now number : " iteration))
    (println (str "now result : " result))
    (if (> iteration number)
      (println (str "result :" result))
      (recur (inc iteration) (+ result iteration)))))
      
(addFrom100 103)

; ex3) Wirte a function, dec-maker, that works exactly like the function inc-maker except with subtraction 
(defn dec-maker
  [number]
  #(- % number))

(def dec9 (dec-maker 9))

(dec9 10)
