; Within each of us lives librarian named Melvil, a fantastical creature who delights in the organizational args. Day and night, Melvil yearns to bring order to your codebase.
; Fortunately, Clojure provide a suite of tools designed specifically to aid this homunculus in its constant struggle against the forces of chaos.

; These tools help you organize your code by goruping together related functions and data. They also prevent name collisions so you don't accidientally orverwirte someone else's code or vice versa. Join me in a tale of suspense and mystery as you learn how to use these tools and solve the heist of a lifetime! By the end of the saga, you'll understand the following :
; What def does
; What namespaces are and how to use them
; The relationship between namespaces and the filesystem
; How to use refer, alias, require, use and ns
; How to organize Clojure projects using the filesystem

;I'll start with a high-level overview of Clojure's organizational system, which works much like a library. Melvil quivers with excitement!

; Your Project as a Library
; Real-world libraries store collections of objects, such as books, magazines, DVDs. They use addressing systems, so when you're given an object's address, you can navigate to the physical space and retrieve the object. 

; Of course, no human being would be expected to know offhand what a book's or DVD's address is. That's why libraries record the association between an object's title and tis address and provide tools for searching these records. In ye olden times before computers, libraries provided card catalogs, which were cabinets filled with paper cards containing each book's title, author, "address" (its Dewy decimal or Library of Congress number), and other info. 

; For exmaple, to find The Da Vinci Code, you would riffle through the title catlaog (cards ordered by title0 until you found the correct card. On that card you would see the address 813.54 (if it's using the Dewey decimal system), navigate your library to find the shelf where The Da Vinci Code resides, and engage in the literary and/or hate-reading adventure of your lifetime. 

; It's useful to imagine a similar setup in Clojure. I think of Cloure as storing objects (like data structures and functions) in a vast set of numbered shelves. No human being could know offhand which shelf an object is stored in. Instead, we give Clojure an identifier that it uses to retrieve the object. 

; For this to be successful, Clojure must maintain the assocications between our identifiers and shelf addresses. It does this by using namespaecs. Namespaces contain maps between human-friendly symbols and references to shelf addresses, known as vars, much like a card catalog. 

; Technically, namespaces are objets of type clojure.lang.Namespace, and you can interact with them just like you can with Clojure data structures. For example, you can refer to the current namespace with *ns*, and you can get its name with (ns-name *ns*):

(ns-name *ns*)

; When you start the REPL, for example, you're in the user namespace (as you can see hear). 

