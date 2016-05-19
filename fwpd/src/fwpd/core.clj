(ns fwpd.core)
(def filename "suspects.csv")

(slurp filename)

; A vector of keys that I'll soon use to create vampire maps.
(def vamp-keys [:name :glitter-index])

; The function covertns a string to an integer.
(defn str->int
  [str]
  (Integer. str))

; The map associates a conversion function with each of the vamp keys.
(def conversions {:name identity :glitter-index str->int}) 

; The function that takes a vamp key and a value, and return the converted value.
(defn convert
  [vamp-key value]
  ((get conversions vamp-key) value))

(convert :glitter-index "3")

((get conversions :name) "hi") 


; The parse function takes a string and 
; first splits it on the newline character to create a seq of strings. 
; Next,it maps over the seq of strings, splitting each one on the comma character.
(defn parse
  "Convert a CSV into rows of columns"
  [string]
  (map #(clojure.string/split % #",")
       (clojure.string/split string #"\r\n")))

(parse (slurp filename))



; The mapify function takes a seq of vectors and 
; combines it with vamp keys to create maps
(defn mapify
  "Return a seq of maps like {:name \"Edward Cullen\":glitter-index 10}"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [vamp-key value]]
                   (assoc row-map vamp-key (convert vamp-key value)))
                 {}
                 (map vector vamp-keys unmapped-row)))
       rows))

(first (mapify (parse (slurp filename))))


;(map vector vamp-keys (parse(slurp filename)))


(defn glitter-filter
  [minimum-glitter records]
  (filter #(>= (:glitter-index %) minimum-glitter) 
          records))

; -> ({:name "Edward " :glitter-index 3} ..)



; Exercises
; 1. Turn the result of your glitter filter into a list of names.

(defn glitter-filter
  [minimum-glitter records]
  (map :name 
       (filter #(>= (:glitter-index %) minimum-glitter) 
                     records)))

(glitter-filter 3 (mapify (parse (slurp filename))))


; 2. Write a function, append , which will append a new suspect to your list of suspects.
;(def suspects (mapify (parse (slurp filename))))

;suspects


; 3. Write a function, validate, which will check that :name and :glitter-index are present when you append. The validate function should accept two arguments :
; 1) a map of keywords to validating functions, similar to conversions,
; 2) and the record to be validated.
(defn validate
  [keyword-map record]
  (map #(% record) keyword-map))

(validate {:name :glitter-index} {:name "hello" :glitter-index 1})

  
(defn keyword-printer
  [keyword-map]
  (println ("key :")))


(:name {:name "melone" :glitter-index 1})



(defn append
  [suspect suspects]
  (conj suspects suspect))

(append {:name "Marine" :glitter-index 10} 
        (mapify (parse (slurp filename))))


(and 1 "hi")


                    
