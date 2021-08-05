(ns sentence-splitter.core
  (:require [clojure.string :as cstr]))


(defn build-splitting-func
  "Based on Regex to build a set of function for sentence splitting
   (by referencing https://stackoverflow.com/a/31505798)"
  []
  (let [
        ;; prefix item-num upper-case-words suffix
        pattern-headline    #"((?:^|[\n])[ ]*(?:[a-z0-9][.][ ]*)?(?:[A-Z]+)(?:[ -](?:[A-Z]|[&-])+)*[ ]*[\n])"
        func-init           (fn [in-str]
                              (-> (str " " in-str " ")
                                  (cstr/replace pattern-headline "$1<stop>")
                                  (cstr/replace #"\n" " ")))
        ;;
        digits              #"([0-9])"
        pattern-float       (re-pattern (str digits #"[.]" digits))
        func-num            (fn [in-str]
                              (-> in-str
                                  (cstr/replace pattern-float "$1<prd>$2")
                                  (cstr/replace #"(No)[.]" "$1<prd>")))
        ;;
        prefixes            #"(Mr|St|Mrs|Ms|Dr|Prof|Capt|Cpt|Lt|Mt)[.]"
        func-prefixes       (fn [in-str]
                              (-> in-str
                                  (cstr/replace prefixes "$1<prd>")
                                  (cstr/replace #"(Ph)[.](D)[.]" "Ph<prd>D<prd>")
                                  (cstr/replace #"(Ph)[.](D)" "Ph<prd>D")))
        ;; legal words
        chapters            #"(Cap|Ch)[.]"
        func-legal          (fn [in-str]
                              (-> in-str
                                  (cstr/replace chapters "$1<prd>")))
        ;;
        websites-head       #"(www)[.]"
        websites-rear       #"[.](com|net|org|io|gov|me|edu)"
        func-websites       (fn [in-str]
                              (-> in-str
                                  (cstr/replace websites-head "$1<prd>")
                                  (cstr/replace websites-rear "<prd>$1")))
        ;;
        alphabets           #"([A-Za-z])"
        starters            #"(Mr|Mrs|Ms|Dr|He\s|She\s|It\s|They\s|Their\s|Our\s|We\s|But\s|However\s|That\s|This\s|Wherever)"
        acronyms            #"([A-Z][.][A-Z][.](?:[A-Z][.])?)"
        suffixes            #"(Inc|Ltd|Jr|Sr|Co)"
        pattern-alpha-start (re-pattern (str #"\s" alphabets #"[.] "))                           ;; start with alpha[.]
        pattern-acronym     (re-pattern (str acronyms #" " starters))                            ;; end with acronym
        pattern-tri-alpha   (re-pattern (str alphabets #"[.]" alphabets #"[.]" alphabets "[.]")) ;; alpha[.]alpha[.]alpha[.]
        pattern-duo-alpha   (re-pattern (str alphabets #"[.]" alphabets #"[.]"))                 ;; alpha[.]alpha[.]
        pattern-suffix-end  (re-pattern (str #" " suffixes #"[.]" starters))                     ;; end with suffix
        pattern-suffix      (re-pattern (str #" " suffixes #"[.]"))
        pattern-alpha       (re-pattern (str #" " alphabets #"[.]"))
        func-start-and-end  (fn [in-str]
                              (-> in-str
                                  (cstr/replace pattern-alpha-start "$1<prd> ")
                                  (cstr/replace pattern-acronym     "$1<stop> $2")
                                  (cstr/replace pattern-tri-alpha   "$1<prd>$2<prd>$3<prd>")
                                  (cstr/replace pattern-duo-alpha   "$1<prd>$2<prd>")
                                  (cstr/replace pattern-suffix-end  "$1<stop> $2")
                                  (cstr/replace pattern-suffix      " $1<prd>")
                                  (cstr/replace pattern-alpha       " $1<prd>")))
        ;;
        prd-quote           #"[.]([”\"])"
        qst-quote           #"[?]([”\"])"
        exp-quote           #"[!]([”\"])"
        func-punc-in-quote  (fn [in-str]
                              (-> in-str
                                  (cstr/replace prd-quote "<prd>$1<stop>")
                                  (cstr/replace qst-quote "<qst>$1<stop>")
                                  (cstr/replace exp-quote "<exp>$1<stop>")
                                  (cstr/replace "..." "<prd><prd><prd>")))
        ;;
        end-punc            #"([.?!])"
        func-final          (fn [in-str]
                              (-> in-str
                                  (cstr/replace end-punc "$1<stop>")
                                  (cstr/replace "<prd>" ".")
                                  (cstr/replace "<qst>" "?")
                                  (cstr/replace "<exp>" "!")
                                  (cstr/trim)))]
    (fn [in-str]
      (-> in-str
          func-init
          func-num
          func-prefixes
          func-legal
          func-websites
          func-start-and-end
          func-punc-in-quote
          func-final
          (cstr/split #"<stop>")))))


(defn build-splitter-params []
  {:splitting-func (build-splitting-func)})


(defn split-sentence
  [text {:keys [splitting-func] :as params}]
  (->> (splitting-func text)
       (mapv #(cstr/trim %))
       (filterv seq)))
