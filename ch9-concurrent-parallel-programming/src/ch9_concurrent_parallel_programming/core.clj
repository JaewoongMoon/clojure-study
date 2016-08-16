(ns ch9-concurrent-parallel-programming.core
  (:gen-class))


; ***************************************************************************
; Chapter 9. THE SACRED ART OF CONCURRENT AND PARALLEL PROGRAMMING

; If I were the lord of a manor and you were my heir, I would sit you down on your 13th name day and tell you, "The world of computing is changing, lass, and ye must be prepared for the new world of multi-core processors lest ye be trampled by it.
; 만약 내가 영주이고 당신이 내 계승자라면, 너의 13번째 생일에 의자에 앉히고 말할 것입니다.
; 컴퓨터 세계는 바뀌고 있단다,얘야. 너는 반드시 새로운 멀티코어 프로세서의 세계에 대비해야 한단다. 그 것들이 너를 짓밟지 못하도록 말이야.   



;<Concurrency and Parallelism Concepts>
; Concurrent and parallel programming invloves a lot of messy details at all levels of program execution, from the hardward to the operating system to programming lanuage libraries to the code that springs from your heart and lands in your editor. 
; 동시성과 병렬 프로그래밍은 많은 지저분한 부분들을 포함합니다. 프로그램 실행, 하드웨어에서부터 OS, 프로그래밍 언어 라이브러리에서부터 코드-여러분의 마음과 땅인 이맥스 에디터로 부터 탄생한-까지 모든 부분에서 말이죠. 
; But before you worry your head with any of those details, in this sections I'll walk through the high-level concepts that surround concurrency and parallelism. 
; 그러나 이번 섹션에서는 그런 머리아픈 세세한 부분보다는 동시성과 병렬 프로그래밍에 대한 대략적 수준의 컨셉만을 다루려고 합니다. 


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




