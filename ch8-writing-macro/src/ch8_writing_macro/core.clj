(ns ch8-writing-macro.core
  (:gen-class))

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


