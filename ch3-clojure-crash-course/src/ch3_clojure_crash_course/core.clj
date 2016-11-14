(ns ch3-clojure-crash-course.core
  (:gen-class))

;************************************************************************
; Chapter 3. Do Things : A Clojure Crash Course
; It's time to learn how to actually do things with Clojre! Hot dammn! Although you've undoubtedly heard of Clojure's awesome concurrency support and other supendous features, Clojure's most salient characteristic is that it is a Lisp. In this chapter, you'll expore the elements that compose this Lisp core:syntax, functions, and data. Together they will give you a solid foundation for representing and solving problems in Clojure.

; After laying this groundwork, you will be able to write some super important code. In the last section, you'll tie everything together by creating a model of a hobbit and writing a function to hit it in a random spot. Super! Important!

; As you move through the chapter, I recommned that you type this examples in a REPL and run them. Programming in a new language is a skill, and just like youdeling or synchromnized swimming, you have to practice to learn it. By the way, Synchronized Swimming for Youdleer for the Brave and True willl be published in August of 20never. Keep an eye out for it!

; Syntax
; Clojure's syntax is simple. Like all Lisps it employs a uniform structure, a handful of special operators, and a constant supply of parentheses deliverd from the parenthesis mindes hidden beneath the Massachusettts Institute of Technology, where Lisp was born.

; Forms
; All Clojure code is written in a uniform structure. Clojure recognizes kinds of structures:
; Literal representations of data stuctures (like numbers, strings, maps, and vectors)
; Operations
; We use the term form to refer to valid code. I'll also sometimes use expression to refer to Clojure forms. But don't get too hung up on the terminology. Clojure evaluates every form to produce a value. These literal representations are all valid forms:
; Your code will rarely contain free-floating literals, of course, because they don't actually do anything on their own. Instead, you'll use literals in operations. Operations are how you do things. All operations take the form opening parenthesis, operator, operads, closing parenthesis:

(str "It was the panda " "in the library" "with a dust buster")

;; CONTROL FLOW
; The do operator lets you wrap up multipole forms in parentheses and run each of them.
; Try the following in your REPL:

; This operator lets you do multiple things in each of the if expression's branches.
; In this case, two things happen:
; Success! is printed in the REPL, and "By Zeus's hammer!" is returned as the value of the
; entire if expression.

(if true
  (do (println "Success!")
      "By Zeus's hammer!")
  (do (println "Failure!")
      "By Aquaman's trident!"))

; You can also omit the else branch. If you do that and the Boolean expression is false,
; Clojure returns nil, like this:
(if false
  "By Odin's Elbow!")
; => nil

; when
; The when operator is like a combination of if and do,
; but with no else branch. Here's an example:
(when true
  (println "Success!")
  "abra cadabra")
; Use when if you want to do multiple things when some condition is true,
; and you always want to return nil when the condition is false.



;;; nil, true, false, Truthniss, Equality, and Boolean Expressions
; Clojure has true and false values. nil is used to indicate no value in Clojure.
; You can check if a vaue is nil with the appropriately named nil? function:
(or false nil :large_I_mean_venti :why_cant_I_just_say_large)

; nil is incicated no value in Clojure
(nil? 1) 
;-> false

(nil? nil) 
;->true

; Both nil and false are used to represent logical falsiness, whereas all other values are
; logically truthy. Truthy and falsey refer to how a value is treated in a Boolean expression,
; like the first expression passed to if:

(if "bears eat beets"
  "bears beets Battlestar Galactica")

(if nil
  "This won't be the result because nil if falsey"
  "nil is falsey")


; Clojure's equality operator is =:

; Some other languages require you to use different operators when comparing values of
; different types. For exmaple, you might have to use some kind of special string
; equlaity operator made just for strings. But you don't need anything weird or tedious
; like that to test for equality when using Clojure's built-int data structures.

; Clojure uses the Boolean operators or and and. or returns either the first truthy value
; or the last value. and returns the first falsey value or, if no values are falsey,
; the last truthy value. Let's look at or first:
(or false nil :large_I_mean_venti :why_cant_I_just_say_large)
; => :large_I?mean_venti
; In the first example, the return value is :large_I_mean_venti because it's the first
; truthy value.

(or (= 0 1) (= "yes" "no"))
; => false
; The second example has no truthy values, so or returns the last value, which is false.

(or nil)
; => nil
; In the last example, once again no truthy values exist, and or returns the last value,
; which is nil.

; Now let's look at and:
(and :fee_wifi :hot_coffee)
; => :hot_coffee
; In the first example, and returns the last truthy value, :hot_coffee.

(and :fellin_super_cool nil false)
; => nil
; In the second example, and returns nil, which is the first falsey value.




;; NAMING VALUES WITH DEF
; You use def to bind a name to a value in Clojure:
(def failed-protagonist-names
  ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"])

failed-protagonist-names
; In this case, you're binding the name failed-protagonist-names to a vector containing
; three strings.

; Notice that I'm using the term bind, whereas in other languages you'd say you're
; assinging a value to a variable. Those other languages typically encourage you to perform
; multiple assignments to the same variable.

; For example, in Ruby you might perform multiple assignments to a variable to build up
; it's value:
severity = :mild
error_message = "OH GOD! IT'S A DISASTER! WE'RE "
if severity == :mild
  error_message = error_message + "MILDLY INCONVENIENCED!"
else
  error_message = error_message + "DOOOOOOOMED!"
end

; You might be templed to do something similar in Clojure:
(def severity :mild)
(def error-message "OH GOD! IT'S A DISASTER! WE'RE ")
(if (= severity :mild)
  (def error-message (str error-message "MILDLY INCONVENIENCED!"))
  (def error-message (str error-message "DOOOOOOOMED!")))

; However, change the value associated with a name like this can make it harder to
; understand your program's behavior because it's more difficult to know which value
; is associated with a name or why that value might have changed. Clojure has a set of
; tools for dealing with change, which you'll learn about in Chapter 10. As you learn
; Clojure,you'll find that you 'll rarely need to alter a name/value association.
; Here's one way you could write the preceding code:
(defn error-message
  [severity]
  (str "OH GOD! IT'S A DISASTER! WE'RE "
       (if (= severity :mild)
         "MILDY INCONVENIENCED!"
         "DOOOOOOOOOOOMED!")))

(error-message :mild)
; Here, you can create a function, error-message, which accepts a single argument,
; everity, and uses that to determine which string to return. You then call the
; function with :mild for the severity. You'll learn all about creating functions in
; "Functions" on page 48; in the meantime, you should treat def as if it's defining
; constants. In the next few chapters, you'll learn how to work with this apparent
; limitation by embracing the functional programming padadigm.



; DATA STRUCTURES
; Clojure comes with a handful of data structures that you'll use the majority of the time.
; If you're coming from an object-oriented background, you'll be surprised at how much
; you can do with the seemingly basic types presented here.

; All of Clojure's data structures are immutable, meaning your can't change them in place.
; For example, in Ruby you could do the following to reassign the failed protagonist name
; at index 0:

failed_protagonist_names = [
  "Larry Potter",
  "Doreen the Explorer",
  "The Incredible Bulk"
]
failed_protagonist_names[0] = "Gary Potter"

failed_protagonist_names
# => [
#   "Gary Potter",
#   "Doreen the Explorer",
#   "The Incredible Bulk"
# ]

; Clojure has no equivalent for this. You'll learn more about why Clojure was implemented
; this way in Chapter 10, but for now it's fun to learn just how to do things without all
; that philosiphizing. Without further ado, let's look at numbers in Clojure. 


; Numbers
93
1.2
1/5

; Strings
; String represent text. The name comes from the ancient Phoenicians, who one day invented
; the alphabet after an accidient involving yarn. Here are some examples of string literals:
"Lord Vordemort"
"\"He who must not be named\""
"\"Great cow of Moscow!\" - Hermes Conrad"
; Notice that Clojure only allows double quotes to delineate strings. 'Lord Voldemort',
; for example, is not a valid string. Also notice that CLojure doesn't have string interpol
;-ation. It only allows concaternation via the str function. 

;; MAPS
; Maps are similar to dictionaries or hashed in other languages.
; They're a way of associating some value with some other value.
; The two kinds of maps in Clojure are hash maps and sorted maps.
; I'll only cover the more basic hash maps. Let's look at some examples of map literals.
; Here's an empty map:

{}
; In this example, :first-anem and :last-name are keywords:
{:fist-name "Charlie"
 :last-name "McFishwich"}

; Here we associate "string-key" with the + function:
{"string-key" +}

; Maps can be nested:
{:name {:first "John" :middle "Jacob" :last "Jing.."}}

; Notice that map values can be of any type-strings, numbers, maps, vectors, even functions.
; Clojure don't care!

; Besides using map literals, you can use the hash-map funciton to create a map:
(hash-map :a 1 :b 2)

; You can look up values in maps with the get function:
(get {:a 0 :b 1} :b)
; => 1

(get {:a 0 :b {:c "ho hum"}} :b)
; => {:c "ho hum"}

; get will return nil if it doesn't find your key, or you can give it a default
; value to return, such as "unicorns?" :

(get {:a 0 :b 1} :c)
; -> nil

(get {:a 0 :b 1} :c "unicorns?")
; -> unicorns

; The get-in function lets you look up values in nested maps:
(get-in {:a 0 :b {:c "ho hum"}} [:b :c])
; -> ho hum
; :b 키의 값을 찾은 다음 그 결과에서 :c 키의 값을 찾는다.

; Another way to look up a value in a map is to treat the map like a funciton with the key
; as its arguments:
({:name "The Human Coffeepot"} :name)
; -> "The Husman Coffeepot"

; Another cool things you can do with maps is use keywords as funcitons to look up their
; values, which leads to the next subject, keywords.



;; KEYWORDS
; Clojure keywords are best understood by seeing how they're used. They're primarily used
; as keys in maps, as you saw in the preceding section. Here are some more examples of key-
; words: 

; Kewywords can be used as functions that look up the corresponding value in a data stucture. 
; For example, you can look up :a in a map:
; ex) look up :a in a map
(:a {:a 1 :b 1 :c 3})
; -> 1
; is equivalent to 
(get {:a 1 :b 2 :c 3} :a)

; with a default value 
(:d {:a 1 :b 2 :c 3} "No gnome knows like Noah knows")
; -> No gnome knows like Noah knows
; Using a keyword as a function is pleasantly succinct, and Real Clojurists do it all the
; time. You should do it too!


;; VECTORS
; A vector is similar to an array, in that it's a 0-indexed collection.
; For example, here's a vector lieteral:
[ 3 2 1]

; Here we're returning the 0th element of a vector:
(get [3 2 1] 0)
; => 3

; nth is slower than get ; Another example of getting by index
(get ["a" {:name "Pulsley Winterbottom"} "c"] 1)
; -> {:name "Pulsley Winterbottom}

; You can create vectors with the vector function:
(vector "creepy" "full" "moon")
;-> ["creepy" "full" "moon"]

; You can use the conj function to add additional elements to the vector.
; Elements are added to the end of a vector:
(conj [1 2 3] 4)
; -> [1 2 3 4]
; Vectors arent't the only way to store sequences; Clojure also has lists.



;; LISTS
; Lists are similar to vectors in that they're linear collections of values.
; But there are some differences. For exmaple, you can't retrive list elements with get.
; To write a list literal, just insert the elements into parentheses and use a single quote
; at the beginning;
 
'(1 2 3 4)
; -> (1 2 3 4)

; Notice that when the REPL prints out the list, it doesn't include the single quote.
; We'll come back to why that happens later, in Chapter 7. If you want to retrive an
; element from a list, you can use the nth function:
(nth '(:a :b :c) 2)
; -> :c

; I don't cover performance in detail in this book because I dont' think it's useful to
; focuse on it until after you've become familiar with a language. However, it's good
; to know that using nth to retrives an element from a list is slower than using get to
; tretrive an element from a vector. This is because Clojure has to traverse all n
; elements of a list to get to the nth, whereas it onyl takes a few hops at most to acess
; a vector element by its index.


; List values can have any type, and you can create lists with the list function.
(list 1 "two" {3 4})

; Elements are added to the beginning of a list (Not like a vector)
(conj '(1 2 3)4)
;-> (4 1 2 3)

; When should you use a list and when should you use a vector? A good rule of thumb is that
; if you need to easily add items to the beginning of a sequence or if you're wiring a macro,
; you should use a list. Otherwise , you should use a vector. As you learn more, you'll get
; a good feel for when to use which.


;; SETS
; Sets are collections of unique values. Clojure has two kinds of sets: hash sets and sorted
; sets. I'll focus on hash sets because they're used more often. Here's the literal notation
; for a hash set:
#{"kurt vonnegut" 20 :icicle}

; Another way to make hash set
(hash-set 1 1 2 2)
; -> #{1 2}

; Note that multiple instances of a value become one unique value in the set, so we're left
; with a single 1 and a single 2. 

(conj #{:a :b} :b)
; -> #{:a :b}

; You can also create sets from existing vectors and lists by using the set function:
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

; Simplicity
; You may have noticed that the treatment of data structures so far doesn't include a desc
; -ription of how to create new types or classes. The reason is that Clojure's emphasis on
; simplicity encourages you to reach for the built-in data structure first.

; If you come from an object-oriented background, you might think that this approach is
; weird and backward. However, what you'll find is that your data does not have to be
; tightly bundled with a class for it to be useful and intelligible. Here's an epigram
; loved by Clojurists that hints at the Clojure philosophy:

; It is better to have 100 functions operate on one data structure than
; 10 functions on 10 data structures. - Alan Perlis

; You will learn more about this aspect of Clojure's philosophy in the coming chapters.
; For now, keep an eye out for the ways that you gain code reusability by sticking to
; basic data structures.

; This concludes our Clojure data structures primer. Now it's time to dig in to
; functions and learn how to use these data structures!


; FUNCTIONS
; One of the reasons people go nuts over Lisps is that these language let you build
; programs that behave in complex ways, yet the primary building block-the functions-is
; so simple. This section initiates you into the beauty and elegance of Lisp functions
; by explaining the following:

; + Calling functions
; + How functions differ from macros and special forms
; + Defining functions
; + Anonymous functions
; + Returning functions 


;; CALLING FUNCTIONS
; By now you've seen many examples of function calls:

; Remember that all Clojure operations have the same syntax:
; opening parenthesis, operator, operands, closing parenthesis.
; Function call is just another term of an operation where the operator is a function
; or a function expression (an expression that returns a function).

; This lets you write some pretty interesting code.
; Here's a function expression that returns the + (addition) function:

(or + -)
; => #object[clojure.core$_PLUS_ 0x5dfc0ba0 "clojure.core$_PLUS_@5dfc0ba0"]
; That return value is the string representation of the addition function.
; Because the return value of or is the first truthy value, and here the addition function
; is truthy, the addition function is returned. You can also use this expression as the
; operator in another exporession:
((or + -) 1 2 3)
; -> 6

; Here are a couple more vailid function calls that each return 6:
((and (= 1 1) +) 1 2 3)
; -> 6

((first [+ 0]) 1 2 3)
; -> 6

; However, these aren't valid funciton calls, because numbers and strings aren't functions:
(1 2 3 4)
("test" 1 2 3)
; -> clojure.lang.Compiler$CompilerException: java.lang.ClassCastException: java.lang.String cannot be cast to clojure.lang.IFn
; You're likely to see this error many times as you continue with Clojure:<x> cannot be cast
; to clojure.lang.IFn just means that you're trying to use something as a function when
; it's not.

; Function flexibility doesn't end with the function expression!
; Syntactically, functions can take any expression as arguments-including other functions.
; Functions that can either take a function as an argument or return a function are called
; higher-order-functions. Programming languages with higher-order-functions are said to
; support first-class functions because you can treat functions as values in the same way
; you treat more familiar data types like numbers an vectors. 

; Take the map function (not to be confused with the map data structure), for instance.
; map creates a new list by applying a function to each member of a collection. Here,
; the inc function increments a number by 1:
(inc 1.1)

; map creates a new list by applying a function to each member of a collection.
(map inc [0 1 2 3])
-> (1 2 3 4)
; (Note that map doesn't return a vector, even though we supplied a vector as an argument.
; You'll learn why in Chapter 4. For now, just trust that this is okay and expected.)

; Clojure's support for first-class functions allows you to build more powerful abstractions
; than you can in languages without them. Those unfamiliar with this kind of programming
; think of functions as allowing you to generalize operations over data instances.
; For exmaple, the + function abstracts addition over any specific numbers.

; By contrast, Clojure (and all Lisps) allows you to create functions that generalize over
; processes. map allows you to generalize the process of transforming a collection by
; applying a function-any function-over any collection.

; The last detail that you need know about functions calls is that Clojure evaluates all
; functions arguments recursively before passing them to the function.
; Here's how Clojure would evaluate a function call whose arguments are also function calls:
(+ (inc 199) (/ 100 (- 7 2)))
(+ 200 (/ 100 (- 7 2))) ; evaluated "(inc 199)"
(+ 200 (/ 100 5)) ; evaluated (- 7 2)
(+ 200 20) ; evaluated (/ 100 5)
220 ; final evaluation

; The function call kicks off the evaluation process, and all subforms are evaluated
; before applying the + function.

; ----------------------------------------------------------------------- 2016.11.14

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

; you can also use rest parameters in let, just like you can in functions : 
(let [[pongo & dalmatians] dalmatian-list]
  [pongo dalmatians])
; -> ["Pongo" ("Perdita" "Puppy 1" "Puppy2")]

(let [[dog & dalmatians] dalmatian-list]
  (recur dalmatians
         (println "name:" dog)))


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



;;
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
