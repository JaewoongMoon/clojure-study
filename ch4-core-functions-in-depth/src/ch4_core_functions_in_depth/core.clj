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

; The following example shows how you could use this capability if you were a vampire trying to curb your human consumption. You have two vectors, one representing human intake in liters and another represeting critter intake for the past four days. The unify-diet-data function takes a single day's data for both human and critter feeding and unifies the two into a single map:

(def human-consumption   [8.1 7.3 6.6 5.0])
(def critter-consumption [0.0 0.2 0.3 1.1])
(defn unify-diet-data
  [human critter]
  {:human human
   :critter critter})

(map unify-diet-data human-consumption critter-consumption)

; Good job laying off the human!
; Another fun thing you can do with map is pass it a collection of functions. You could use this if you wanted to perform a set of calculations on different collections of numbers, like so :

(def sum #(reduce + %))

(def avg #(/ (sum %) (count %)))

(defn stats
  [numbers]
  (map #(% numbers) [sum count avg]))


(stats [3 4 10])

(stats [80 1 44 13 6])

; In this example, the stats function iterates over a vector of functions, applying each function to numbers. 
; Additionally, Clojurists often use map to retrieve the value associated with a keyword from a collection of map data structures. Because keywords can be used as functions, you can do this succinctly. Here's an example:

(def identities
  [{:alias "Batman" :real "Bruce Wayne"}
   {:alias "Spider-Man" :real "Peter Parker"}
   {:alias "Santa" :real "Your mom"}
   {:alias "Easter Bunny" :real "Your dad"}])

(map :real identities)

; REDUCE
; Chapter 3 showed how reduce processes each element in a sequence to build a result. This section shows couple of other ways to use it that might not be obvious. 
; The first use is to transform a map's value, producing a new map with the same keys but with updated values:
(reduce (fn [new-map [key val]]
          (assoc new-map key (inc val)))
        {}
        {:max 30 :min 10})
; In this example, reduce treats the argument {:max 30 :min 10} as a sequence of vectors, like ([:max 30][:min 10]). Then, it starts with an empty map (the second argument) and builds it up using the first argument, an anonymous function. It's as if reduce does this :

(assoc (assoc {} :max (inc 30))
  :min (inc 10))

; The function assoc takes three arguments: a map, a key, and a value. It derives a new map from the map you give it by associating the given key with the given value. For exmaple, (assoc {:a 1} :b 2) would return {:a 1 :b 2}
; Another use for reduce is to filter out keys from a map based on their value. In the following exmaple, the anonymous funciton checks whether the value of key-value pair is greather than 4. If it isn't , then the key-value pair is filtered out. In the map {:human 4.1 :critter 3.9}, 3.9 is less than 4, so the :critter key and its 3.9 value are filtered out. 

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

; Use filter to return all elements of a sequence that test true for a predicate function. 
(filter #(< (:human %) 5) food-journal)

; Often, you want to know whether a collection contains any values that test true for a predicate function. The some function does that, return the first truthy value (any value that's not false or nil) returned by a predicate function: 
(some #(> (:critter %) 5) food-journal)

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

;Macros allow you to transform arbitrary expressions into valid Clojure, so you can extend the language itself to fit your needs. And you don't even have to be a wizened old dude or lady in a robe to use them!
; To get just a sip of this power, consider this trivial macro:
(defmacro backwards
  [form]
  (reverse form))

(backwards (" backwards" " am" "I" str))
; -> I am backwards

;The backwards macro allows Clojure to successfully evaluate the expression (" backwards" " am" "I" str), even though it doesn't follow Clojure's build-in syntax rules, which require an expression's operand to appear first (not to mention the rule that an expression not be written in reverse order). Without backwards, the exporession would fail harder than milennia of alchemists ironically spending their entire lives pursuing an impossible means of achieving immortality. With backwards, you've created your own syntax! You've extended Clojure so you can write code however you please! Bettern than turning lead into gold, I tell you!

; This chapter gives you the conceptual foundation you need to go mad with power writing your own macros. It explains the elements of Clojure's evaluation model: the reader, the evaluator, and the macro expander. It's like the periodic table of Clojure elements. Think of how the periodic table reveals the properties of atoms: elements in the same column behave similarly because they have the same nuclear charge. Without the periodic table and its underlying theory, we'd be in the same position as the alchemists of yore, mixing stuff together randomly to see what blow up. But with a deeper understanding of the elements, you can see why stuff blows up and learn how to blow stuff up on purpose. 

;<An Overview of Clojure's Evaluation Modl>
;Clojure (like all Lisps) has an evaluation model that differs from most other languages: it has a two-phrase system where it read textual source code, producing Clojure data structures. These data structures and performs actions like function application or var lookup based on the type of the data structure. For example, when Clojure reads the text (+ 1 2), the result is a list data structure whose first element is a + symbol, followed by the numbers 1 and 2. This data structure is passed to Clojure's evaluator, which looks up the function correspt of your onding to + and applies that function corresponding to + and applies that funciton to 1 and 2.

; Languages that have this relationship between source code, data, and evaluation are called homoiconic. (Incidentally, if you say hmoiconic in front of your bathroom mirror three times with the lights out, the ghost of John McCarthy appears and hands you a parenthesis.) Homoiconic languages empower you to reason about your code as a set of data structurs that you can manipulate programmically. To put this into context, let's take a jaunt through the land of compilation. 

; Programming languages require a compiler or interpreter for translating the code you write, which consists of Unicode characters, into something else: machine instructions, code in another programming language, whatever. During this process, the compiler constructs an abstract syntax tree (AST), which is a data structure that represents your program. You can think of the AST as the input to the evaluator, which you can think of as a function that traverses the tree to produce the machine code as its output. 

; So far this sounds a lot like what I described for Clojure. 
; However, in most languages the AST's data structure is inaccessible within the programming language; the programming language space and the compiler space are forever separated, and never the twain shall meet. Figure 7-1 shows how you might visualize the compilation process for an expression in a non-Lis programming language. 

; 1 + 6 * 7 
; Step 1. Text gets parsed and lexed by parser & lexer
; Step 2. which pops out a happy little AST 
; Step 3. This goes to the evaluator
; Step 4. which makes machine code or whatever

; But Clojure is different, because Clojure is a Lisp and Lisps are hotter than a stolen tamale. Instead of evaluating an AST that's represented as some inaccessible internal data structure, Lisps evaluate native data structures. Clojure still evaludates tree structures, but the trees are structured using Clojure lists and the nodes are Clojure values. 

; Lists are ideal for constructing tree structures. The first element of a list is treated as the root, and each subsequent element is treated as a branch. To create a nested tree, you can just nested lists, an shown in Figure 7-2.

; (+ 1 2)
; + is the first element which treated as the root
; (+ 1 (* 6 7))
; nested lists are treated as nested trees
; First, Clojure's reader converts the text  (+ 1 (* 6 7)) into a nested list. (You'll learn more about the reader in the next section. ) Then, Clojure's evaluator takes that data as input and produces a result. (It also compiles Java Virtual Machine (JVM) bytecode, which you'll learn about in Chapter 12. For now, we'll just focues on the evaluation model on a conceptual level.)

; With this in mind, Figure 7-3 show what Clojure's evaluation process looks like.


; <S-Expressions>
; In your Lisp adventures, you'll come across resources that explain that Lisps evaluate s-expressions. I avoid that term here because it's ambiguous: you'll see it used to refer to both the actual data object that gets evaludated and the source code that represents the data. Using the same term for two different components of Lisp evaluation (code and data) obscures what's important: your text represents native data structures, and Lisps evaluate native data structures, which is unique and awesome. For a great greatment of s-expressions, check out ...

; step 1. Text goes to the reader
; step 2. which pops out a happy little Clojure list that totally accessible in program
; step 3. This goes to the evaluator. 
; step 4. which return a value

; However, the evalutor doesn't actually care where its input comes from; it doesn't have to come from the reader. As a result, you can send your program's data structures directly to the Clojure evaluator with eval. Behold!

(def addition-list (list + 1 2))
(eval addition-list)

; That's right, baby! your program just evaluated a Clojure list. 
; You'll read all about Clojure's evaluation rules soon, but briefly, this is what happend: when Clojure evaluated the list, it looked up the list that addition-list refers to; then it looked up the function corresponding to the + symbol; and then it called that function with 1 and 2 as arguments, return 3. The data structures of your running program and thse of the evaluator live inthe same space, and the upshot is taht you can use the full power of Clojure and all the code you've written to construct data structures for evaluation: 

(eval (concat addition-list [10]))
; => 13
(eval (list 'def 'luckey-number (concat addition-list [10])))
; => #'user/lucky-number

luckey-number
; => 13

; Your program can talk directly to its own evaluator, using its own functions and data to modify itself as it runs! Are you goin mad with power yet! I hope so! Hold on to some of your sanity, though, because there's still more to learn. 

; So Clojure is homoicoic: it represents a abstract syntax trees using lists, and you write textual representations of lists when you write Clojure code. Because the code you write represents data structures that you're used to manipulating and the evaluator consumes those data structures, it's easy to reason about how to programmatically modify your program. 

; Macros are what allow you to perform those manipulations easily. The rest of this chapter overs Clojure's reader and evaluation rules in detail to give you a precise understaing of how macros work.


; <The Reader>
; The reader converts the textual source code you save in a file or enter in the REPL into Clojure data structures. It's like a translator between the human world of Unicode characters and Clojure's world of lists, vectors, maps, symbols, and other data structures. In this section, you'll interact directly with the reader and learn how a handy feature, the reader macro, lets you write code more succinctly. 

; <The Reading>
; To understand reading, let's first take a close look at how Clojure handles the text you type in the REPL. First, the REPL prompts you for text:
; user =>
; Then you enter a bit of text. Maybe something like this:
; That text is really just a sequence of Unicode characters, but is't meant to represent a combination of Clojure data structures. This textual representation of data structure is called a reader form. In this example, the form represents a list data stucture that contains three more forms: the str symbol and two strings. 

; Once you type those characters into the prompt and press enter, that text goes to the reader (remember REPL stands for read-eval-print-loop). Clojure reads the steam of characters and internally produces the corresponding data structures. It then evaluates the data structures and prints the textual representation of the result:

; Reading and evaluation are discrete processes that you can perform independently. One way to interact with the reader directly is by using the read-string function. read-string takes a string as an argument and processes it using CLojure's reader, returning a data structure:
(read-string "(+ 1 2)")

(list? (read-string "(+ 1 2)"))

(conj (read-string "(+ 1 2)") :zagglewag)

; In the first example, read-string reads the string representation of a list comtaining a plus symbol and the numbers 1 and 2. The return value is an actual list, as proven by the second example. The last example uses conj to prepend a keyword to the list. The takewasy is that reading and evaluating are independent of each other. You can read text without evaluating it, and you can pass the result to other functions. You can also evaluate the result, if you want:

(eval (read-string "(+ 1 2)"))

; In all the examples so far, there's been a one-to-one relationship between the reader form and the corresponding data structures. Here are more examples of simple reader forms that directly map to the data structures they represent:
; -. () A list reader form
; -. str A symbol reader form 
; -. [1 2] A vector reader form containing two number reader forms
; -. {:sound "hoot"} A map reader form with a keyword reader form and string reader form 

; However, the reader can employ more complex behavior when converting text to data structures. For example, remember anonymous functions?
(#(+ 1 %) 3)
; => 4
; Well, try this out:
(read-string "#(+ 1 %)")
; => (fn* [p1__423#] (+ 1 p1__423#))
; Whoa! This is not the one-to-one mapping that we're used to. Reading #(+ 1 %) somehow resulted in a list consisting of the fn* symbol, a vector containing a symbol, and a list containing three elements. What just happened?


; <Reader Macros>
; I'll answer my own question; the reader used a reader macro to transform #(+ 1 %). Reader macros are sets of rules for transforming text into data structures. They often allow you to represent data structures in more compact ways because they take an abbreviated reader form and expand it into a full form. They're designated by macro characters, like '(the single quote), #, and @. They're also completely different from the macros we'll get to later. So as not to get the two confused, I'll always refer to reader macros using the full term reader macros. 

; For example, you can see how the quote reader macro expands the single quote character here:
(read-string "'(a b c)")
; => (quote (a b c))
; When the reader encounters the single quote, it expands it to a list whose first member is the symbol quote and whose second member is the data structure that fo llowed the single quote. The deref reader macro works similarly for the @ character:
(read-string "@var")
; => (clojure.core/deref var)
;Reader macros can also do crazy stuff like cause text to be ignored. The semicolon designates the single-line comment reader macro:
(read-string "; ignore!\n(+ 1 2)")

;And that's the reader! Your humble companion, toiling away at transforming text into data structures. Now let's look at how Clojure evaluates those data structures.

;<The Evaluator>
; You can think of Clojure's evaluator as a function that takes a data structure as an argument, processes the data structure using rules corresponding to the data structure's type, and returns a result. To evaluate a symbol, Clojure looks up what the symbol refers to. To evaluate a list, Clojure looks at the first element of the list, Clojure looks at the first element of the list and calls a function, macro, or special form. Any other values (including string, numbers, and keywords) simply evaluate to themselves.

; For example, let's say you've type (+ 1 2) in the REPL. Figure 7-5 shows a diagram of the data structure that gets sent to the evaluator. 

; Because it's a list, the evaluator starts by evaluating the first element in the list. The first element is the plus symbol, and the evaluator resolves that by returning the corresponding function. Because the first element in the list is a function, the evaluator evaluates each of the operands. The operands 1 and 2 evaluates to themselfves because they're not lists or symbols. Then the evaluator calls the addition function with 1 and 2 as the operands, and returns the result. 

; The rest of this section explains the evalutor's rules for each kind of data structure more fully. To show how the evaluator works, we'll just run each example in the REPL. Keep in mind that the REPL first reads your text to get a data structure, then send that data structure to the evaluator, and then prints the result as text.

;<Data>
; I write about how Clojure evaluates data structures in this chapter, but that's imprecise. Technically, data structures refers to some kind of collection, like a liked list or b-tree, or whatever, but I also use the term to refer to scalar (singular, noncoilleciton) values like symbols and numbers. I considered using the term data objects but didn't want to imply object-oriented programming, or using just data but didn't want to confuse that with data as a concept. So, data structure it is, and if you find this offensive, I will give you a thousand apoligies, thoughtfully organized in a Van Emde Boas tree.

;<These Things Evaluate to Temselves>

; Whenever Clojure evaluates data structures that aren't a list or symbol, the result is the data structure itself:
true
false
{}
:huzzah
;Empty lists evaluates to temselves, too:
()

;<Symbols>
; One of your fundamental tasks as a programmer is creating abstractions by associating anems with values. You learned how to do this in Chapter 3 by using def, let, and function definitions. Clojure uses symbols to name functions, macros, data, and anythong else you can use, and evaluates them by resolving them. To resolve a symbol, Clojure traverses any bindings you've created and then lookup the symbol's entry in a namespace mapping, which you learned about in Chapter 6. Ultimately, a symbol resolves to either a value or a special form-a built-in Clojure operator that provides fundamental behavior. 

; In general, Clojure resolves a symbol by:
; 1. Looking up whether the symbol names a special form. If it doesn't...
; 2. Looking up whether the symbol corresponds to a local binding. If it doesn't...
; 3. Trying to find a namespace mapping introduced by def. If it doesn't...
; 4. Throwing an exception

; Let's first look at a symbol resolving to a pecial form. Special forms, like if, are always used in the context of an operation; they're alwasy the first element in a list:
(if true :a :b)
; In this case, if is a special form and it's being used as an operator. If you try to refer to a special form outside of this context, you'll get an exception:

if
; => compilerException
; Next, let's evaluate some local bindings. A local biding is any association between a symbol and a value that wasn't created by def. In the next example, the symbol x is bound to 5 using let. When the evaluator resolves x, it resolves the symbol x to the value 5:
(let [x 5]
  (+ x 3))
; => 8
; Now if we create a namespace mapping of x to 15, Clojure resolves it accordingly:
(def x 15)
(+ x 3)
; => 18
; In the next example, x is mapped to 15, but we introduce a local binding of x to 5 using let. So x is resolved to 5:
(def x 15)
(let [x 5]
  (+ x 3))
; You can nets bindings, in which case the most recently defined binding takes precedence:
(let [x 5]
  (let [x 6]
    (+ x 3)))
; => 9
; Functions also create local bindings, binding parameters to arguemtns within the function body. In this next example, exclaim is mapped to a function. Within the function body, the parameter name exclamation is bound to the argument passed to the function : 
(defn exclaim 
  [exclamation]
  (str exclamation "!"))

(exclaim "Hadoken")
; Finally, in this last example, map and inc both refer to functions:
(map inc [1 2 3])
; When Clojure evaluate this code, it first evaluates the map symbol, looking up the corresponding function and applying it to its arguments. The symbol map refers to the map function, but it shouldn't be confused with the function itself. The map symbol is still a data structure, the same whay that the string "fried salad" is a data structure, but it's not the same as the function itself:

(read-string "+")
; => error?

(type (read-string "+"))
; => clojure.lang.Symbol

(list (read-string "+") 1 2 )
; => (+ 1 2)
; In these examples, you're interacting with the plus symbol, +, as a data structure. You're not interacting with the addition function that it fefers to. If you evaluate it, Clojure looks up the function and applies it:
(eval (list (read-string "+") 1 2 ))

; On their own, symbols and their referents don't actually do anything; Clojure performs work by evaluating lists. 


;<Lists>
; If the data structure is an empty list, it evaluates to an empty list:
(eval (read-string "()"))
; => ()
; Otherwise, it is evaluated as a call to the first element in the list. The way the call is performe depends on the nature of that first element. 

; <Function calls>
; When performing a function call, each operand is fully evaluated and then passed to the function as an argument. In this example, the + symbol resolves to a function:
(+ 1 2)
; => 3
; Clojure sees that the list's head is a function, so it proceeds to evaluate the rest of the elements in the list. The operands 1 and 2 both evaluate to themselves, and after they're evaluated, Clojure applies the addition function to them. 

; You can also nest function calls:
(+ 1 (+ 2 3))
; => 6
; Even though the second cargument is a list, Clojure follows the same process here: look up the + symbol and evaluate each argument. To evaluate the list (+ 2 3), Clojure resolves the first member to the addition function and proceeds to evaluate each of the arguments. In this way, evaluation is recursive. 

;<Special Forms>
; You can also call special forms. In general, special forms are speical because they implement core behavior that can't be implemneted with functions. For example, 
(if true 1 2)

; Here, we ask Clojure to evaluate a list beginning with the symbol if. That if symbol gets resolved to the if special form, and Clojure calls that special form with the operands true, 1, and 2.

; Special forms don't follow the same evaluation rules as normal functions. For example, when you call a function, each operand gets evaluated. However, with if you don't want each operand to be evaluated. You only want certain operands to be evaluated, depending on whether the condition is true or false.

; Another important special form is quote. You've seen lists represented like this:
'(a b c)

; As you saw in "The Reader" on page 153, this invokes a reader macro so that we end up with this:
(quote (a b c))

; Normally, Clojure would try to resolve the a symbol and then call it because it's the first element in a list. The quote special form tells the evaluator, "Instead of evaluating my next data structure like normal, just return the data structure itself." In this case, you end up with a list consisting of the symbols a, b, and c.

; def, let, loop, fn, do, and recur are all special forms as well. You can see why: they don't get evaluated the same way as functions. For example, normally when the evaluator evaluates a symbol, it resolves that symbol, but def and let obviously don't behave that way. Instead of resolving symbols, they actually create associations between symbols and values. So the evaluator receives a combination of data structures from the reader, and it goes about resolving the symbols and calling the functions or special forms at the beginning of each list. But there's more! you can also place a macro at the beginning of a list instead of a function or a special form, and this can give you tremendous power over how the rest of the data structures are evaluated. 

; <Macros>
; Hmmm...Clojure evaluates data structures-the same data structures that we write and manipulate in our Clojure programs. Wouldn't it be awesome if we could use Clojure to manipulate the data structures that Clojure evaluattes? Yes, yes it would! And guess what? You can do this with macros! Did your head just explode? Mine did!

; To get an idea of what macros do, let's look at some code. Say we want to write a function that makes Clojure read infix notation (such as 1 + 1) instead of its normal notation with the operator first (+ 1 1). This example is not a macro. Rather, it merely show that you can write code using infix notation and then use Clojure to transfrom it so it will actually execute. First, create a list that represents infix addition:

(read-string "(1 + 1)")
; => (1 + 1)
; Clojure will throw an exception if you try to make it evaluate this list:
(eval (read-string "(1 + 1)"))
; => ClassCastException
; However, read-string returns a list, and you can use Clojure to reorganize that list into something it can successfully evaluate:
(let [infix (read-string "(1 + 1)")]
  (list (second infix) (first infix) (last infix)))
;=> (+ 1 1)

; If you eval this, it returns 2, just as you'd expect:
(eval (let [infix (read-string "(1 + 1)")]
  (list (second infix) (first infix) (last infix))))
;=> 2
; This is cool, but it's also quite clunky. That's where macros come in. Macros give you a convenient way to manipulate lists before Clojure evaluates them. Macros are a lot like functions: they take arguments and return a value, just like a function would. They work on Clojure data structures, just like functions do. What makes them unique and powerful is the way they fit in to the evaluation process. They are executed in between the reader and the evaluator-so they can manipulate the data structures that the reader spits out and transform with those data structures before passing them to the evaluator. 

; Let's look at an example:
(defmacro ignore-last-operand
  [function-call]
  (butlast function-call))

(ignore-last-operand (+ 1 2 10))
; => 3

;; This will not print anything
(ignore-last-operand (+ 1 2 (println "look at me!!!")))
; => 3
; At 1, the macro ignore-last-operand receives the list (+ 1 2 10) as its argument, not the value 13. This is very different from a function call, because function calls always evaluate all of the arguments passed in, so there is no possible way for a function to reach into one of its operands and alter or ignore it. By contrast, when you call a macro, the operands are not evaluated. In particular, symbols are not resovled; they are passed as symbols. Lists are not evaluated either; that is, the first element in the list in not called as a funciton, speical form, or macro. Rather, the unevaluated list data structure is passed in. 

; Another difference is that the data structure returned by a function is not evaluated, but the data structure returned by a macro is. The process of determining the return value of a macro is called macro expansion, and you can use the function macroexpand to see what data structure a macro returns before that data structure is evaluated. Note that you have to quote the form that you pass to macroexpand:
(macroexpand '(ignore-last-operand (+ 1 2 10)))
; => (+ 1 2)
; As you can see, both expansions result in the list (+ 1 2). When this list is evaluated, as in the previous example, the result is 3. 

; Just for fun, here's a macro for doing simple infix notation:
(defmacro infix
  [infixed]
  (list (second infixed)
        (first infixed)
        (last infixed)))

(infix (1 + 2))
; => 3
; The best way to think about this wholle process is to picture a phase between reading and evaluation: the macro expansion phas. Figure 7-6 show how you can visualize the entire evaluation process for (infix (1 + 2)).

; And that's how macros fit into the evaluation process. But why would you want to do this? The reason is that macros allow you to transform an arbitrary data structures like (1 + 2) into one that can Clojure can evaluate, (+ 1 2)/ That means you can use Clojure to extend itself so you can write programs however you please. In other words, macros enabe syntactic abstraction. Syntactic abstraction may sound a bit abstract(ha ha!), so let's explore that a little.

; <Syntactic Abstraction and the -> Macro>
; Often, Clojure code consists of a bunch of nested function calls. For example, I use the following function in one of my projects:
(defn read-resource
  "Read a resource into a string"
  [path]
  (read-string (slurp (clojure.java.io/.resource path))))

; To understand the function body, you have to find the innermost form, in this case (clojure.java.io/resource path), and then work your way outward from right to left to see how the result of each function gets passed to another function. This right-to-left flow is opposite of what non-Lisp programmers are used to. As you get used to writing in Clojure, this kind of code gets easier and easier to understand. But if you want to translate Clojure code so you can read it in a more familiar, left-to-wright, top-to-bottom manner, you can use the built-in -> macro, which is also known as the threading or stabby macro. It lets you rewrite the preceding function like this:
(defn read-resource
  [path]
  (-> path
      clojure.java.io/resource
      slurp
      read-string))

; You can read this as a pipeline that goes top to bottom instead of from inner parentheses to outer parentheses. First, path gets passed to io/resources, then the result gets passed to slurp, and finally the result of that gets passed to read-string. 

; These two ways of defining read-resource are entirely equivalent. However, the second one migth be easier understand because we can approach it from top to bottom, a direction we're used to. The -> also lets us omit parentheses, which means there's less visual noise to contend with. This is a syntactic abstraction because it lets you write code in a syntax that's different from Clojure's built-in syntax but is preferable for human consumption. Better than lead into gold!!!


; <Summary>
; In this chapter, you learned about Clojure's evaluation process. First, the reader transforms text into Clojure data structures. Next, the macro expander transfroms those data structures with macros, converting your custom syntax into syntactically valid data structures. Finally, those data structures based on their type:symbols are resolves to their referents; lists result in function, macro, or special form call; and everything else evaluates to itself. 

; The coolest thing about this process is that it allow you to use Clojure to expand its own syntax. This process is made easier because Clojure is homoiconic: its text represent abstract syntax trees, allowing you to more easily reason about how to constrcut syntax-expanding macros.

; With all these new concepts in your brainacles, you're now ready to blow stuff up on purpose, just like I promised. The next chapter will teach you everything  you need to know about writing macros. Hold on to your socks or they're liable to get knocked off! 


; *******************************************************************************
 ; Chapter 8. WRITING MACROS
; When I was 18, I got a job as a night auditor at a hotel in Santa Fe, New Mexico, working four nights a week from 11 pm till 7 am. After a few months of this sleepless schedule, my emotions took on a life of their own. One night, at about 3 am, I was watching an informercial for a product claiming to restore men's hair. As I watched the stoiry of a formerly bald indivisual, I became overwhelmed with sincere joy. "At last!" my brain gushed. "This man has gotten the love and success he deserves! What an incredible product, giving hope to the hopeless!"

; Since then I've found myself wondering if I could somehow recreate the emotional abandon and appreciation for life induced by chronic sleep deprivation. Some kind of potion, perhaps-a couple quaffs to unleash my inner Richar Simmons, but not for too long. 

; Just as a potion would allow me to emprorarily alter my fundamental nature, macros allow you to modify Clojure in ways that just aren't possible with other languages. With macros, you can extend Clojure to suit your problem space, building up the language. 

; In this chapter, You'll thotoughly examine how to write macros, starting with basic examples and moving up in complexity. You'll close by donning your make-belive cap and using macros to validate customer orders in your imaginary online potion store. 
; By the end of the chapter, you'll understand all the tools you'll use to write macros: quote, syntax quote, unquote, unquote splicing (aka the pinata tool), and gensym. You'll also learn about the dangers lying in wait for unsuspecting macro authors: double evaluation, variable capture, and macro infection.

;<Macros Are Essential>
; macros allow Clojure to derive a lot of its built-in functionality from a tiny core of functions and special forms. Take when, for exmaple, when has this general form: ...
; You might think that when is a special form like if. 
; However, when is actually a macro. 
; In this macro expansion, you can see that when is implemented in terms of if and do:
(macroexpand '(when boolean-expression
                expression-1
                expression-2
                expression-3))
; => (if boolean-expression
;     (do expression-1
;         expression-2
;         expression-3))
; This shows that macros are an integral part of Clojure development-they're even used to provide fundamental operations. Macros aren't reserved for exotic special cases; you should think of macro writing as just another tool in your tool stachel. 

; <Anatomy of a Macro>
(defmacro infix
  "Use this macro when you pine for the notation of your childhood"
  [infixed]
  (list (second infixed) (first infixed) (last infixed)))

(infix (1 + 1))
; => 2
; One key difference between functions and macros is that function arguments are fully evaluated before they're passed to the function, whereas macros receive arguements as unevaluated data. 
(macroexpand '(infix (1 + 1)))
; => (+ 1 1)

; You can also use argument destucturing in macro definitions, just like you can with functions:
(defmacro infix-2
  [[operand1 op operand2]]
  (list op operand1 operand2))

(infix-2 (1 + 2))
; => 3
; Destructuring arguments lets you succinctly bind values to symbols based on their position in a sequential argument. 
; Here, infix-2 takes a sequential data structure as an argument and 
; destructures by position so the first value is named operand1, the second value is named op, and the third value is named operand2 within the macro. 

; You can also create multiple-arity macros, and in fact the fundamental Boolean operations and and or are defined as macros. Here's and 's source code:
(defmacro and
  "Evaluates exprs one at a time, from left to right. If a form returns logical false (nil or false), and returns that value and doesn't evaluate any of the other expressions, otherwise it returns the value of the last expr. (and) returns true."
  {:added "1.0"}
  ([] true)
  ([x] x)
  ([x & next]
   '(let [and# ~x]
      (if and# (and ~@next) and#))))

; There's a lot of stuff going on in this example, including the symbols ' and ~@, which you'll learn about soon. What's important to realize for now is that there are three macro bodies here:a 0-arity macro body that always returns true, a 1-arity macro body that return the operand, and an n-arity macro body that recursively calls itself. That's right:macros can be recursive, and they also can use rest args (& next in the n-arity macro body), just like functions. 



;...
; The single quote character is a reader macro for (quote x )
'(+ 1 2)

;<Syntax quoting>
; Let's compare quoting and syntax quoting. 
; quoting does not include a namespace if your code doesn't include a namespace:
'+
; => +

; Write out the namespace, and it'll be returned by normal quote:
'clojure.core/+
; => clojure.core/+

; Syntax quoting will alway include the symbol's full namespace:
`+
;=> clojure.core/+

; Quoting a list recursively quotes all the elements:
'(+ 1 2)

; Syntax quoting a list recursively syntax quotes all the elements:
`(+ 1 2)
; => (clojure.core/+ 1 2)
; The reason syntax quotes include the namespace is to help you avoid name collisions, a topic covered in Chapter 6. 

; The other difference between quoting and syntax quoting is that the latter allows you to unquote forms using the tilde, ~. It's kind of like kryptonite in that way: whenever Superman is around kryptonite, his powers disappear. Whenever a tilde appears within a syntax-quoted form, the syntax quote's power to return unevaluated, fully namespaced forms disappears.
`(+ 1 ~(inc 1))
; => (clojure.core/+ 1 2)

(list '+ 1 (inc 1))
;=> (+ 1 2)
`(+ 1 ~(inc 1))
;=> (clojure.core/+ 1 2)


; <Using Syntax Quoting in a Macro>
; code-critic ver.1 (Using plain quoting)
(defmacro code-critic
  "Phrases are courtesy Hermes Conrad from Futurama"
  [bad good]
  (list 'do
        (list 'println
              "Great squid of Madrid, this is bad code:"
              (list 'quote bad))
        (list 'println
              "Sweet gorilla of Manila, the is good code:"
              (list 'quote good))))

(code-critic (1 + 1) (+ 1 1))

; code-critic ver.2  (Using syntax quoting)
(defmacro code-critic
  "Phrases are courtesy Hermes Conrad from Futurama"
  [bad good]
  `(do (println "Great squid of Madrid, this is bad code:"
                (quote ~bad))
       (println "Sweet gorilla of Manila, the is good code:"
                (quote ~good))))

; To sum up, macros receive unevaluated, arbitrary data structures as arguments and return data structures that Clojure evaluates. When defining your macro, you can use argument destructuring just like you can with functions and let bindings. you can also write multiple-arity and recursive macros. 

; Most of the time, your macros will return lists. you can build up the list to be returned by using list functions or by using syntax quoting. Syntax quoting usually leads to code that's clearer and more concise because it lets you create a template of the data structure you want to return that's easier to parse visually. Whether you use syntax quoting or plain quoting, it's important to be clear about the distinction between a symbol and the value it evaluates to when building up your list. And if you want your macro to return multiple forms for Clojure to evaluate, make sure to wrap them in a do.

;<Refactoring a Macro and Unquote Splicing>
; First, let's create a function to generate those println lists. 
; Functions are easier to think about and play with than macros, so it's often a good idea to move macro guts to helper functions:
; Helper function
(defn criticize-code
  [criticism code]
  `(println ~criticism (quote ~code)))

; code-critic ver.3 (Using helper function)
(defmacro code-critic
  [bad good]
  `(do ~(criticize-code "Cursed bacteria of Liberia, this is bad code:" bad)
       ~(criticize-code "Sweet sacred boa of Westeros, this is good code :" good)))

; code-critic ver.4 (Using map function)
(defmacro code-critic
  [bad good]
  `(do ~(map #(apply criticize-code %)
               [["Great squid of Madrid, this is bad code:" bad]
                ["Holy goriila of Manila, this is good code:" good]])))

(code-critic (1 + 1) (+ 1 1))
;=> NullPointerException
; The problem is that map returns a list, and in this case, it returned a list of println expressions. We just want the result of each println call, but instead, this code sticks both results in a list and then tries to evaluate that list. 
; println evaluates to nil, so we end up with something like (nil nil). 
; nil isn't callable, and we get a NullPointerException. 

; Unquote splicing was invented precisely to handle this kind of situation. 
; Unquote splicing is performed with ~@
`(+ ~(list 1 2 3))
; => (clojure.core/+ (1 2 3))
`(+ ~@ (list 1 2 3))
; => (clojure.core/+ 1 2 3)

; code-critic ver.5 (Add Unquote splicing)
; Unquote slicing 을 사용함으로써 println을 평가한 결과가 아닌 println 그 자체 expression을 얻을 수 있다. 
(defmacro code-critic
  [good bad]
  `(do ~@(map #(apply criticize-code %)
               [["Great squid of Madrid, this is bad code:" bad]
                ["Holy goriila of Manila, this is good code:" good]])))

(code-critic (1 + 1) (+ 1 1))

;<Things to Watch Out For>
; Macros have a couple of sneaky gotchas that you should be aware of. In this section, you'll learn about some macro pitfalls and how to avoid them. I hope you haven't unstrapped yourself from your thinking mast. 

; Variable Capture
; Variable capture occurs when a macro introduces a bingding that, unknown to the maro's user, eclipses an existing binding. For example, in the following code, a macro mischievously introduces its own let binding, and that messes with the code:

(def message "Good Job!")
(defmacro with-mischief
  [& stuff-to-do]
  (concat (list 'let ['message "Oh, big deal!"])
          stuff-to-do))

(with-mischief
  (println "Here's how I feel about that thing you did: " message))

; 매크로 내에서 전역변수와 동일한 이름의 변수를 사용해버리면 전역변수가 아닌 매크로내에서 정의한 변수 값이 사용되어 버린다. 
; 매크로를 사용하는 입장에서는 마치 버그처럼 보인다. 
; 이 것이 Variable Capture 이다. 

; Notice that this macro didn't use syntax quoting. Doing so would result in an exception:
; Syntax Qutoing을 사용할 때 Variable capture가 발생하는 경우 Exception을 발생하도록 되어있다. 
(def message "Good job!")

(defmacro with-mischief
  [& stuff-to-do]
  `(let [message "Oh, big deal!"]
     ~@stuff-to-do))

(with-mischief
  (println "Here's how I feel about that thing you did: " message))
; => Compiler Exception , Can't qualified name : message

; This exception is for your own good: syntax quoting is designed to prevent you from accidentally capturing variables within macros. 
; If you want to introduce let bindings in your macro, you can use a gensym. 
; The gensym function produces unique symbols on each successive call:
(gensym)
; => G__1760

; You can also pass a symbol prefix:
(gensym 'message)
; => message1792
(gensym 'message)
; => message1808

; Here's how you could rewrite with-mishchief to be less mischievous:
(defmacro without-mischief
  [& stuff-to-do]
  (let [macro-message (gensym 'message)]
    `(let [~macro-message "Oh, big deal!"]
       ~@stuff-to-do
       (println "I still need to say: " ~macro-message))))

(without-mischief
  (println "Here's how I feel about that thing you did: " message))
; => Here's how I feel about that thing you did : Good job!
; => I still need to say: Oh, big deal!

; This example avoids variable capture by using gensym to create a new, unique symbol that then gets bound to macro-message. 
; Within the syntax-quoted let expression, macro-message is unquoted, resolving to the gensym'd symbol. 
; Tis gensym'd symbol is distinct from any symbols within stuff-to-do, so you avoid variable capture. 
; Because this is such a common pattern, you can use an auto-gensym. Auto-gensyms are more concise and convenient ways to use gensyms:
`(blarg# blarg#)

`(let [name# "Moon Jae Woong"] name#)
; In this example, you create an auto-gensym by appending a hash mark (or hashtag, if you must insist) to a symobl within a syntax-quoted lst. 

; <Double Evaluation>
; Another gotcha to watch out for when wiriting macros is double evaluation, which occurs when a form passed to a macro as an argument gets evaluated more than once. Consider the following:
(defmacro report
  [to-try]
  `(if ~to-try
     (println (quote ~to-try) "was successful:" ~to-try)
     (println (quote ~to-try) "was not successful:" ~to-try)))

;; Thread/sleep takes a number of milliseconds to sleep for
(report (do (Thread/sleep 1000) (+ 1 1)))

; This code is meant to test its argument for truthiness. If the argument is truthy, it's considered successful; if it's falsey, it's unsuccessful. The macro prints whether or not its argument was successful. In this case, you would actually sleep for two seconds because (Thread/sleep 1000) gets evaluated twice: once right after if and again when println gets called. This happens because the code (do (Thead/sleep 1000) (+ 1 1) is repeated throughout the macro expansion. 

; let 을 사용해서 to-try가 평가된 결과를 gensym지역변수에 저장함으로서 동일한 식이 여러번 평가되는 것을 막을 수 있다.  
(defmacro report
  [to-try]
  `(let [result# ~to-try]
     (if result#
        (println (quote ~to-try) "was successful:" result#)
        (println (quote ~to-try) "was not successful:" result#))))


; <Macros All the Way Down>
(report (= 1 1))

`(+ 1 ~(inc 1))
`(println (quote ~(= 1 1)) "was successful:" ~(= 1 1))
; 식앞에 ~을 붙이면 그 식은 평가된다. 
; 따라서 위의 ~to-try는 True로 평가될 것 같다. 
; 그런데 거기에 quote를 붙이면 그 식은 파라메터를 그대로 텍스트로 출력한 것처럼 보이게 된다.
; 도대체 어떻게 된 것일까?

(list 'quote (= 1 1))

; -----------------------------------------------------------------------------
; 2016. 8. 1 (Mon)
; <Validation Functions>
; 실전에서 어떤 상황일 때 매크로를 쓰면 되는지 알려준다. 그 예로 값 검증 프로그램을 든다. 
; To keep things simple, we'll just worry about validating the name and email for each order. For our store, I'm thinking we'll want to have those order details represented like this:
(def order-details
  {:name "Mitchard Blimmmons"
   :email "mitchard.blimmonsgamil.com"})

; ideal email validation code
; 아래와 같은 식으로 Validation 코드를 작성하고 싶다.  
; validate 함수에 
; 검증이 되는 값인 order-details 아규먼트와 
; 검증을 하는 로직과 메세지가 들어있는 order-details-validations 아규먼트를 넣는다. 
; (validate order-details order-details-validations)
; => {:email ["Your email address doesn't look like an email address."]}

;That is, we want to be able to call a function, validate, with the data that needs validation and a definition for how to validate it. The result should be a map where each key corresponds to an invalid field, and each value is a vector of one or more validation messages for that field. 

(def order-details-validations
  {:name
   ["Please enter a name" not-empty]
   
   :email
   ["Please enter an email address" not-empty
    
    "Your email address doesn't look like an email address"
    #(or (empty? %) (re-seq #"@" %))]})

(defn error-messages-for
  "Return a seq of error messages"
  [to-validate message-validator-pairs]
  (map first (filter #(not ((second %) to-validate))
                     (partition 2 message-validator-pairs))))

; error-messages-for 에 대한 설명 : 첫번재 파라메터 to-validate는 검증될 값이고, 두 번째 파라메터 message-validator-pairs 는 짝수 개의 엘레먼트가 짝지은 상태여야 한다.  
; The first argument, to-validate, is the field you want to validate.
; The second argument, message-validate-pairs, should be a seq with an even number of elements. This seq gets grouped into pairs with (partition 2 message-validator-pairs).

; message-validator-pairs 에 대한 설명: 첫번째 파라메터는 에러메세지가 되어야 하고, 두번째 파라메터는 펑션이 되어야 한다. 
;The first element of the pair should be an error message, and the second element of the pair should be a function (just like the pairs are arranged in order-details-validations). 

; error-messages-for　펑션은 모든 에러메세지와 검증펑션 짝을 필터링한다. 왜? 
;The error-messages-for function works by filtering out all error message and validation pairs where the validation function returns true when applied to to-validate. 
; 그 후에 필터링된 각 짝에서 첫번째 요소 - 에러메세지를 추출한다. 
; It then uses map first to get the first element of each pair, the error message. Here it is in action:
(error-messages-for "" ["Please enter a name" not-empty])
; => ("Please enter a name")


(not-empty "")


; 2016. 8. 2 (Tues)
(defn validate
  "Returns a map with a vector of errors for each key"
  [to-validate validations]
  (reduce (fn [errors validation]
            (let [[fieldname validation-check-groups] validation
                  value (get to-validate fieldname)
                  error-messages (error-messages-for value validation-check-groups)] 
              (if (empty? error-messages)
                errors
                (assoc errors fieldname error-messages))))
          {}
          validations))

(validate order-details order-details-validations)
; => {:email ("Your email address doesn't look like an email address")}
; Success! This works by reducing over order-details-validations and associating the error messages (if there are any) for each key of order-details into a final map of error messages.

; <if-valid>
; Most often, validation will look something like this:
(let [errors (validate order-details order-detail-validations)]
  (if (empty? errors)
    (println :success)
    (println :failure errors)))

; The pattern is to do the following:
; 1. Validate a record and bind the result to errors
; 2. Check whether there were any errors
; 3. If there were, do the success thing, here (println :success)
; 4. Otherwise, do the failure thing, here (println :failure errors)

; This wouldn't work, because success-code and failure-code would get evaluated each time. A macro would work because macros let you control evaluation. 
(defn if-valid
  [record validations success-code failure-code]
  (let [errors (validate record validations)]
    (if (empty? errors)
      success-code
      failure-code)))

; Here's how you'd use the macro:
(if-valid order-details order-detail-validations errors
          (render :success)
          (render :failure errors))

; This macro hides the repetitive details and helps you express your intention more succinctly. 
; Here's the implementation:
(defmacro if-valid
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ~@then-else)))

; This macro takes four arguments: to-validate, validations, errors-name, and the rest argument then-else. Using error-name like this is a new strategy. We want to have access to the errors returned by the validate function within the then-else statements. To do this, we tell the macro what symbol it should bind the result to. The following macro expansion shows how this works. 
(macroexpand
 '(if-valid order-details order-details-validations my-error-name
            (println :success)
            (println :failure my-error-name)))
;=> (let* [my-error-name (ch4-core-functions-in-depth.core/validate order-details order-details-validations)] (if (clojure.core/empty? my-error-name) (println :success) (println :failure my-error-name)))
; The syntax quote abstracts the general form of the let/validate/if pattern you saw earlier. Then we use unquote splicing to unpack the if branches, which were packed into the then-else rest argument. 

; ***************************************************************************
; Chapter 9. THE SACRED ART OF CONCURRENT AND PARALLEL PROGRAMMING

; If I were the lord of a manor and you were my heir, I would sit you down on your 13th name day and tell you, "The world of computing is changing, lass, and ye must be prepared for the new world of multi-core processors lest ye be trampled by it.
; 만약 제가 영주이고 당신이 내 계승자라면, 그대의 13번째 생일에 의자에 앉히고 말할 것입니다.
; 컴퓨터 세계는 바뀌고 있단다,얘야. 너는 반드시 새로운 멀티코어 프로세서의 세계에 대비해야 한단다. 너를 그 것들이 짓밟지 못하도록 말이야.   

; There are three central challenges in concurrent programming.
; The first Concurrency Goblin : the reference cell problem. 
; The reference cell problem occurs when two threads can read and write to the same location, and the value at the location depends on the order of the reads and writes. 
; The sencond Concurrency Goblin is mutual exclusion. Imagine two threads, each trying to write a spell to a file. 
; The thirtd Concurrency Golbin is deadlock. 

; <Futures, Delays, and Promises>
; Futures, Delays, and Promises are easy, lightweight tools for concurrent programming. 
; When you write serial code, you bind together these three events:
; - Task definition
; - Task execution
; - Requiring the task's result

; <Futures>
; In Clojure, you can use futures to define a Task 
; and place it on another thread without requiring the result immediately. 
; You can create future with the future macro. 
(future (Thread/sleep 4000)
        (println "I'll print after 4 seconds"))

(let [result (future (println "this prints once")
                     (+ 1 1))]
  (println "dref: " (deref result))
  (println "@: " @result))


; Dereferencing a future will block if the future hasn't finished running, like so:
(let [result (future (Thread/sleep 3000)
                     (+ 1 1))]
  (println "dref: " (deref result))
  (println "@: " @result))

; Sometimes you want to place a time limit on how long to wait for a future. 
; To do that, you can pass deref a number of millisenconds to wait along with the value to return if the deref times out:
(deref (future (Thread/sleep 1000) 0) 10 5)
; This code tells deref to return the value 5 if the future doesn't return a value within 10 milliseconds. 

; Finally, you can interrogate a future using realizaed? to see if it's done running:
(realized? (future (Thread/sleep 1000)))
; => false

(let [f (future)]
  @f
  (realized? f))
; => true

; Futures are a dead-simple way to sprinkle some concurrency on your program. 
; On their own, they give you the power to chuck tasks onto other threads, which can make your program more efficient. They also let your program behave more flexibly by giving you control over when a task's result is required.


; <Delays>
; Delays allow you to define a task without having to execute it or require the result immediately. You can create a delay using delay:
(def jackson-5-delay
  (delay (let [message "Just call my name and I'll be there"]
           (println "First deref:" message)
           message)))

(force jackson-5-delay)
; Like futures, a delay is run only once and its result is cached.
; Subsequent dereferencing will return the Jackson 5 message without printing anything:
@jackson-5-delay
; One way you can use a delay is to fire off a statement the first time one future out of a group of related futures finishes. 
; For example, pretend your app uploads a set of headshots to a headshot-sharing site and notifies the owner as soon as the first one is up, as in the following:

; define a vector of headshots to upload (gimli-headshots)
(def gimli-headshots ["serious.jpg" "fun.jpg" "playful.jpg"])
; two functions("email-user", "upload-document") to pretend-perfrom the two operations. 
(defn email-user
  [email-address]
  (println "Sending headshot notification to" email-address))
(defn upload-document
  "Needs to be implemented"
  [headshot]
  true)
; use "let" to bind "notify" to a delay. 
; The body of the delay, "(email-user "and-my-axe@gmail.com")", isn't evaluated when the delay is created. Instead, it gets evaluated the first time one of the futures created by the "doseq" form evaluates "(force notify)"
; Even though "(force notify)" will be evaluated three times, the delay body is evaluated only once. 
(let [notify (delay (email-user "and-my-axe@gmail.com"))]
  (doseq [headshot gimli-headshots]
    (future (upload-document headshot)
            (force notify))))

; This technique can help protect you from the mutual exclusion Concurrency Golbin - the problem of making sure that only one thread can access a particular resource at a time. In this example, the delay guards the email server resource. Because the body of a delay is guranteed to fire only once, you can be sure that you will never run into a situation where two threads send the same email. Of course, no thread will ever be able to use the delays to send an email again. That might be too drastic a constraint for most situations, but in cases like this example, it works perfectly. 


; <Promises>
; Promises allow you to express that you expect a result without having to define the task that should produce it or when that task should run. You create promises using promise and deliver a result to them using deliver. You obtain the result by dereferencing:
; 프로미스를 사용해서 태스크를 정의하지 않고 사용가능하다. 
; 나중에 값이 필요할 때 정의해서 쓰면된다.
(def my-promise (promise))
(deliver my-promise (+ 1 2))
@my-promise
; => 3
; Here, you create a promise and then deliver a value to it. 
; Finally, you obtain the value by dereferencing the promise. 
; Dereferencing is how you express that you expect a result, and if you had tried to dereference my-promise without first delivering a value, the program would block until a promise was delivered, just like with futures and delays. You can only deliver a result to a promise once. 
; promise 에게 결과를 배달(deliver)할 수 있는 것은 처음의 한 번 뿐이다.  


; One use for promises is to find the first satisfactory element in a collection of data. Suppose, for example, that you're gathering ingredients to make your parrot sound like James Earl Jones. 
; promises 의 한 가지 사용법중 하나는 데이터 컬렉션 중에서 조건을 만족하는 첫 번째 element를 찾을 때 쓰는 것이다. 

; The following code defines some yak butter products, creates a function to mock up an API call, and creates another function to test whether a product is satisfactory:

(def yak-butter-international
  {:store "Yak Butter International"
   :price 90
   :smoothness 90})
(def butter-than-nothing
  {:store "Butter Than Nothing"
   :price 150
   :smoothness 83})
;; This is the butter that meets our requirements
(def baby-got-yak
  {:store "Baby Got Yak"
   :price 94
   :smoothness 99})

; The API call waits one second before returning a result to simulate the time it would take to perform an actual call. 
(defn mock-api-call
  [result]
  (Thread/sleep 1000)
  result)

(defn satisfactory?
  "If the butter meets our criteria, return the butter, else return false"
  [butter]
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))

; To show how long it will take to check the sites synchronously, we'll use "some" to apply the "satisfactory?" function to each element of the collection and return the first truthy result, or nil if there are none. When you check each site synchronously, it could take more than one second per site to obtain a result, as the following code shows:
; Here I've used "comp" to compose functions, and I've used "time" to print the time taken to evaluate a form. You can use a promise and futures to perform each check on a seperate thread. 
(time (some (comp satisfactory? mock-api-call)
            [yak-butter-international butter-than-nothing baby-got-yak]))

; If your computer has multiple cores, this could reduce the time it takes to about one second. 
(time 
 (let [butter-promise (promise)]
(doseq [butter [yak-butter-international butter-than-nothing baby-got-yak]]
     (future (if-let [satisfactory-butter (satisfactory? (mock-api-call butter))]
               (deliver butter-promise satisfactory-butter))))
   (println "And the winner is:" @butter-promise)))

; In this example, 
; 1) you first create promise , "butter-promise" and then 
; 2) create three futures with across to that promise. 
; Each future's task is to evaluate a yak butter site and 
; to deliver the site's data to the promise if it's satisfactory.
; 3) Finally, you dereference "butter-promise", 
; causing the program to block until the site data is delivered.
; This takes about one second instead of three because the site evaluations happen in parallel. 
; By decoupling the requirement for a result from how the result is actually computed, you can perform multiple computations in parallel and save some time. 
; 결과에 대한 요구와 결과를 계산하는 로직을 떼어냄(decoupling)으로서 여러 개의 태스크를 병렬로 수행할 수 있게 되었다. 그리고 시간을 절약할 수 있게 되었다. 


; You can view this as a way to protect yourself from the reference cell Concurrency Goblin. 
; 위와 같은 방법을 reference cell 동시성 고블린의 공격을 막는데에 쓸 수 있습니다. 
; Because promises can be written to only once, you prevent the kind of inconsistent state that arises from nondeterministic reads and writes. 
; 왜나햐면 프로미스는 단 한번만 쓰여지기 때문에, 비결정적인 읽기나 쓰기를 방지할 수 있기 때문이죠. 

; You migth be wondering what happens if none of the yak butter is satisfactory. 
; 만약 조건에 맞는 버터를 찾지 못한다면 어떻게 되는 것일까 하고 궁금할 수 있습니다.
; If that happens, the dereference would block forever an tie up the thread. 
; 만약 그런 상황이 발생하면, dereference는 스레드를 영원히 잡고 있을 겁니다.
; To avoid that, you can include a timeout:
; 그 것을 피하기 위해, 우리는 timeout 을 사용할 수 있습니다.
(let [p (promise)]
  (deref p 100 "timed out"))
; This creates a primise, p, and tries to dereference it. 
; 이 것은 p 라는 프로미스를 만든 뒤, 그 것을 dereference 하려는 시도를 합니다. 
; The number 100 tells deref to wait 100 milliseconds, 
; 숫자 100은 deref에게 결과를 100 밀리초(0.1초)만큼까지는 기다려보라라고 말하는 것입니다.  
; and if no value is available by then,
; 그리고 만약 아무런 값도 전달받지 못한다면,
; to use the timeout value, "timed out".
; "timed out" 이라는 문자열을 사용합니다.

; The last detail I should mention is that you can also use promises to register callbacks, achieving the same functionality that you might be used to in JavaScript. 
; 마지막으로 덧붙이고 싶은 것은, 독자가 promise를 콜백을 정의(register)하는 데에 사용할 수도 있다는 것입니다. JavaScript 개발할 때 사용해왔던 것 처럼요. 
; JavaScript callbacks are a way of defining code that should execute asynchronously once some other code finishes. 
; JavaScript 콜백이란 [다른 코드가 실행을 완료했을 때 비동기적으로 실행되는] 코드를 정의하는 방법입니다. 
; Here's how to do it in Clojure:
; Clojure에는 어떻게 하는지 봅시다. 
(let [ferengi-wisdom-promise (promise)]
  (future (println "Here's some Ferengi wisdom:" @ferengi-wisdom-promise))
  (Thread/sleep 100)
  (deliver ferengi-wisdom-promise "Whisper your way to success."))

; This example creates a future that begins executing immediately. 
; 이 예제는 future를 만든 후 바로 실행합니다.
; However, the future's thread is blocking because it's waiting for a value to be delivered to "ferengi-wisdom-promise". 
; 그러나 future스레드는 블로킹되는데요. 왜냐하면 "ferengi-wisdom-promise"에 배달되는 값을 기다리기 때문입니다.
; After 100 millisenconds, you deliver the value and the "println" statement in the future runs.
; 100 밀리초가 지난후에, 값이 배달되고, future 내에 있는 println이 실행됩니다.
; Futures, delays, and promises are great, simple way to manage concurrency in your application. 
;Futures, delays, promises 는 훌륭합니다. 그리고 어플리케이션의 동시성을 관리하는 단순한 방법입니다.
;In the next section, we'll look at one more fun way to keep your concurrent applications under control.
; 다음 섹션에서는 동시성 어플리케이션을 만드는 데 필요한 한가지 더 재밌는 방법을 살펴볼 겁니다.
;

; <Rolling Your Own Queue>
; So far you've looked at some simple ways to combine futures, delays, and promises to make your concurrent programs a little safer. 
; 지금까지 우리는 futures, delays, promises를 조합해서 동시성 프로그램을 조금 더 안전하게 만드는 간단한 방법을 살펴봤습니다.
; In this section, you'll use a macro to combine futures and promises in a slightly more complex manner. 
; 이번 섹션에서는 조금 더 복잡한 방법을 사용해서 future와 proimses 조합하기 위해 매크로를 사용할 겁니다.
; You might not necessarily ever use this code, but it'll show the power of these modest tools a bit more. 
; 아마 이 코드를 사용할 일은 없겠지만, 이 코드는 이 별것 아닌 툴이 가진 힘을 더 보여줄 겁니다.
; The macro will require you to hold runtime logic and macro expansion logic in your head at the same time to understand what's going on;
; 매크로는 런타임 로직과 매크로 확장 로직을 당신의 머리속에서 그리는 동시에 무슨 일이 일어나고 있는지 이해하는 것을 요구할 겁니다.  
; if you get stuck, just skip ahead.
; 만약 막히게 되면 그냥 스킵해도 좋습니다.

; One characteristic The Three Concurrency Goblins have in common is that they all involve tasks concurrently accessing a shared-resource-a variable, a printer, a dwarven war axe-in an uncoordinated way. 
; 세가지 동시성 고블린의 공통된 특징중 하나는 셋다 공유 자원(변수, 프린터, 드워프 액스) 등에 조직화되지 않은 방법으로 접근한다는 것입니다. 
; If you want to ensure that only one task will access a resource at a time, you can place the resource access portion of a task on q queue that's executed serially. 
; 만약 자원에 접근하는 것을 한번에 하나씩으로 강제하고 싶다면, 자원 접근을 하는 업무를 큐에 넣고 연속적으로 실행해야 합니다. 
; It's kind of like making a cake: you and a friend can separately retrieve the ingredients (eggs, flour, eye of newt, what have you), but some steps you'll have to perform serially.  

; To implement the queuing macro, you'll pay homage to the British, because they invented queues. 
; 큐 매크로를 구현하려면 먼저 영국인들에게 존경을 표해야합니다. 왜냐하면 그들이 큐를 발명했으니까요. 

;This demonstration will invlove an abundance of "sleep"ing, so here's a macro to do that more concisely:
(defmacro waits 
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))
; All this code does is take whatever forms you give it and insert a call to "Thread/sleep" before them, all wrapped up in "do"
; 이 코드가 하는 일은 제공된 어떤 폼이든지 받아들인 후에, 폼이 실행되기 전에 "Thead/sleep"을 요청하고, 모든 것을 "do"로 감싸는 것입니다. 




