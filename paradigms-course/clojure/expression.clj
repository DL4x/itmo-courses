(load-file "parser.clj")

;; Functional part
(def constant constantly)

(defn variable [value]
  #(get % value))

(defn abstract-unary-operation [foo]
  (fn [value]
    #(foo (value %))))

(def negate (abstract-unary-operation -))
(def exp (abstract-unary-operation #(Math/exp %)))
(def ln (abstract-unary-operation #(Math/log %)))

(defn abstract-binary-operation [foo]
  (fn [value1 value2]
    #(foo (value1 %) (value2 %))))

(def add (abstract-binary-operation +))
(def subtract (abstract-binary-operation -))
(def multiply (abstract-binary-operation *))
(def divide (abstract-binary-operation #(/ (double %1) %2)))

;; Object part
(defn proto-get
  ([obj key] (proto-get obj key nil))
  ([obj key default]
   (cond
     (contains? obj key) (get obj key)
     (contains? obj :proto) (recur (get obj :proto) key default)
     :else default)))

(defn proto-call [obj key & args]
  (apply (proto-get obj key) obj args))

(defn field
  ([key] (field key nil))
  ([key default] (fn [obj] (proto-get obj key default))))
(def _value (field :value))

(defn method [key]
  (fn [obj & args] (apply proto-call obj key args)))
(def evaluate (method :evaluate))
(def toString (method :toString))
(def toStringPostfix (method :toStringPostfix))

(defn constructor [constr proto]
  (fn [& args] (apply constr {:proto proto} args)))

(def Constant (constructor
                (fn [this value]
                  (assoc this :value value))
                {
                 :evaluate (fn [this _] (_value this))
                 :toString (fn [this] (str (_value this)))
                 :toStringPostfix toString
                 }))

(def Variable (constructor
                (fn [this value]
                  (assoc this :value value))
                {
                 :evaluate (fn [this vars] (get vars (str (Character/toLowerCase (first (_value this))))))
                 :toString (fn [this] (_value this))
                 :toStringPostfix toString
                 }))

(defn Abstract-Operation [foo mathSymbol]
  (constructor
    (fn [this & args]
      (assoc this :value (vec args)))
    {
     :evaluate (fn [this vars] (apply foo (mapv #(evaluate % vars) (_value this))))
     :toString (fn [this] (str "(" mathSymbol " " (clojure.string/join " " (mapv #(toString %) (_value this))) ")"))
     :toStringPostfix (fn [this] (str "(" (clojure.string/join " " (mapv #(toStringPostfix %) (_value this))) " " mathSymbol ")"))
     }))

(def Negate (Abstract-Operation - "negate"))
(def Sin (Abstract-Operation #(Math/sin %) "sin"))
(def Cos (Abstract-Operation #(Math/cos %) "cos"))
(def Inc (Abstract-Operation #(+ % 1) "++"))
(def Dec (Abstract-Operation #(- % 1) "--"))
(def Add (Abstract-Operation + "+"))
(def Subtract (Abstract-Operation - "-"))
(def Multiply (Abstract-Operation * "*"))
(def Divide (Abstract-Operation #(/ (double %1) %2) "/"))

;; Functional / Object parser part
(def functional-operations
  {
   :constant constant
   :variable variable
   'negate negate
   'exp exp
   'ln ln
   '+ add
   '- subtract
   '* multiply
   '/ divide
   })

(def object-operations
  {
   :constant Constant
   :variable Variable
   'negate Negate
   'sin Sin
   'cos Cos
   '+ Add
   '- Subtract
   '* Multiply
   '/ Divide
   })

(defn parser [element operations]
  (letfn [(parse-brackets [element]
            (apply (get operations (first element)) (mapv #(parser % operations) (rest element))))]
    (cond
      (number? element) ((get operations :constant) element)
      (symbol? element) ((get operations :variable) (str element))
      (list? element) (parse-brackets element))))

(defn parseFunction [expression] (parser (read-string expression) functional-operations))
(defn parseObject [expression] (parser (read-string expression) object-operations))

;; Combined parser part
(def *space (+char "\u0009\u000A\u000B\u000C\u000D\u0020\u0085\u00A0\u1680\u180E\u2000\u2001\u2002
                    \u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u2028\u2029\u202F\u205F\u3000"))

(defn sign [s tail]
  (if (#{\+ \-} s)
    (cons s tail)
    tail))
(def *digit (+char ".0123456789"))
(def *number (+map read-string (+str (+seqf sign (+opt (+char "+-")) (+plus *digit)))))

(def *letter (+str (+plus (+char "xyzXYZ"))))

(def parser-operations
  {
   "negate" Negate
   "+" Add
   "++" Inc
   "-" Subtract
   "--" Dec
   "*" Multiply
   "/" Divide
   })

(def parseObjectPostfix
  (let
    [*constant (+map Constant *number)
     *variable (+map Variable *letter)
     *white-space (+ignore (+star *space))
     *math-symbol (+str (+plus (+char "++--*/negate")))]
    (letfn [(*value []
              (+or *constant *variable *math-symbol (*brackets)))
            (*seq [begin p end]
              (+seqn 1 (+char begin) (+star (+seqn 0 *white-space p *white-space)) (+char end)))
            (*brackets []
              (+map #(apply (get parser-operations (last %)) (drop-last %)) (*seq "(" (delay (*value)) ")")))]
      (+parser (+seqn 0 *white-space (*value) *white-space)))))
