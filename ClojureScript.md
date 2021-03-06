# A Brief ClojureScript Primer

This guide is intended as a mini-tutorial and quick-reference to ClojureScript. Most of these examples will also work in normal Clojure, so can be tried out using `lein repl` in your terminal.

Comments in Clojure use a semicolon `;` and continue to the end of the line. `; => ` in the examples denotes the result of evaluating the expression.

In Clojure, any commas `,` are treated exactly the same as whitespace. They are usually omitted.

* [Primitives](#primitives)
* [Function calling](#function-calling)
* [Data Structures](#datastructures)
* [Variables](#variables)
* [Output](#output)
* [Loops](#loops)
* [Conditionals](#conditionals)
* [Defining Functions](#defining-functions)
* [Destructuring](#destructuring)
* [Namespaces](#namespaces)
* [Atoms](#atoms)
* [JavaScript](#javascript)
* [Other Links](#other-links)

## Primitives

**Numbers** are always doubles, because JavaScript.
```clojure
1
1.5
35672342364
7e5
```

**Booleans** also exist.
```clojure
true
false
```

**Strings** are double quoted.
```clojure
"and basically"
"do what you'd expect"
```

**nil** represents `null`, and has to exist because of the host platform.
```clojure
nil
```

**Keywords** are symbolic identifiers, they provide very fast equality tests, and so are often used as keys in associative datastructures.
```clojure
:keywords
:can-also-contain-dashes
```

## Function calling

Like all lisps, clojure uses polish notation to apply functions. The program is expressed as a series of expressions wrapped in parentheses. The first item is the function to apply, and all additional items are the function's arguments. These "S-expressions" can be nested as much as needed.

```clojure
(inc 1)
; => 2
(+ 1 2 3 4 5 6)
; => 21
(> (+ 1 2 3) (- 10 4 5))
; => true
```

## Data Structures

Clojure and ClojureScript provide very powerful immutable collections. Each has different properties, but any function that "modifies" one of these structures will always return a new (inexpensively copied) value.

See the cheatsheet linked at the bottom of the page for functions that can operate on these datastructures. Many of these functions work on all every datastructure by treating them all as sequences - known in clojure as `seq`s.

**Lists** are linked lists, they support efficient adding and removing from the front of the list.
```clojure
(list 1 2 3)
; => (1 2 3)

(conj (list 3 2 1) 4)
; => (4 3 2 1)
```

**Vectors** are sequences that support efficient random access and append.
```clojure
(vector 1 2 3)
; => [1 2 3]
[1 2 3 4]
; => [1 2 3 4]
(conj [1 2 3] 4)
; => [1 2 3 4]
(nth [1 2 3] 1)
; => 2
```

**Hash-maps** are key-value collections with undefined order. There is a variant **sorted-map** which keeps the keys in a defined order. We often use keywords as the keys.
```clojure
(hash-map :a 1 :b 2)
; => {:a 1, :b 2}
{:hello "World", :store "Stuff"}
; => {:hello "World", :store "Stuff"}

; assoc and dissoc add/remove values
(assoc {:a 1} :b 2)
; => {:a 1, :b 2}
(dissoc {:a 1 :b 2} :b)
; => {:a 1}

; maps can be called as functions to get values
({:a 1 :b 2} :b)
; => 2
; keywords are also getter functions
(:a {:a 1 :b 2})
; => 1

; there are *-in variants for nested maps
(assoc-in {:a {:b 1}} [:a :b] 42)
; => {:a {:b 42}}
; update-in lets us apply a function to a nested item
(update-in {:a {:b 1}} [:a :b] inc)
; => {:a {:b 2}}

(sorted-map 2 :b 1 :a)
=> {1 :a, 2 :b}
```

## Variables

Clojure doesn't like to call anything a variable, its equivalents come in two flavours, local and global.

Clojure's stores global values in `vars`. They are defined with `def`. All references to vars are late-bound, meaning that redefining a var will mean all existing references point to the latest value. There is also a variation on this called `defonce`, this does the same as `def`, but will be ignored if the var is already defined.
```clojure
(def foo 1)

foo
; => 1

(def foo 2)

foo
; => 2

(defonce bar 1)

bar
; => 1

(defonce bar 2)

bar
; => 1
```

Local names can be bound using the `let` form. The form takes a vector of bindings, which is a concept re-used in many other functions and macros. A binding vector is simply series of name-value pairs.

```clojure
(let [foo 1
      bar 2]
  (+ foo bar))
; => 3
```

## Output

Clojure provides a few core functions for getting output to the console. In ClojureScript this is usually set up to print to the browser console, but it can be changed using `set-print-fn!`.

`println` is used to get a human readable representation with a newline on the end.
```clojure
(prn [1 2 3])
; => [1 2 3]

(prn "Can" "take" "a bunch" [:of :arguments])
; => "Can" "take" "a bunch" [:of :arguments]
```

`print`, `prn` and `pr` are related and also sometimes useful.

## Loops

Clojure doesn't really do loops in the traditional sense. The usual preference is to favour higher order functions like `map`, `filter` and `reduce`. See the Sequences section in the cheatsheet for more.

The `for` form works much like a list comprehension. It uses a binding like the let form that is applied to every item in the input sequence and describes how to create a new sequence.

```clojure
(for [a (range 10) :when (odd? a)]
  (* a a))
; => (1 9 25 49 81)
```

## Conditionals

The basic conditional is the `if` form.

```clojure
(if true
  :when-true
  :when-false)
; => :when-true

(if false
  :when-true
  :when-false)
; => :when-false
```

Other variations will expand internally into an `if`. Examples include `if-not`, `when`, `cond`, `case` and `condp` among others. See the cheatsheet for more.

## Defining Functions

Functions are defined using the `fn` form, which takes a vector of local names to bind and then the body of the function.

```clojure
(fn [a b] (+ a (* a b)))
; => fn

((fn [a b] (+ a (* a b))) 2 2)
; => 6
```

This can get a bit annoying to type, but we can use `defn` to `def` and `fn` at the same time.

```clojure
(defn twice [w] (list w w))

(twice "you")
; => ("you" "you")
```

There's also a shorthand for doing anonymous functions, the `%` symbol is replaced with the first argument. `%2`, `%3` etc can be used for more arguments.

```clojure
(map #(* 3 %) [1 2 3])
; => [3 6 9]
```

## Destructuring

To further reduce boilerplate in binding forms and function definitions, we can use data structures on the left hand side of assignments to unpack variables. Note that this is not pattern matching, if the destructuring form doesn't match we will either get a runtime error or nil values.

We can destructure vectors in `let`, `for` and funtions.
```clojure
(let [[a b] [1 2]]
  (+ a b))
; => 3

(defn repeat-pair [[a b :as v]]
  (conj v a b))
(repeat-pair [1 2])
; => [1 2 1 2]

(for [[k v] {:a 1 :b 2 :c 3}]
  (repeat v k))
; => ((:c :c :c) (:b :b) (:a))
```

We can also destructure maps with the `:keys` shorthand when they have keyword keys.
```clojure
(let [{:keys [a b c]} {:a 1 :b 2 :c 3}]
  (+ a b c))
; => 6
```

[Full docs on destructuring](http://clojure.org/special_forms#Special%20Forms--Binding%20Forms%20%28Destructuring%29)

## Namespaces

Clojure organises code into namespaces, the module loading system expects namespaces to match filenames.

Namespaces are dot-separated, and defined with the `ns` form. Vars from different namespaces can be referred to with a fully-qualified name separated by `/`. When at the REPL, the current namespace is displayed at the prompt, and can be changed by calling `ns` again.

```clojure
(ns my.little.namespace)
(defn plus2 [n] (+ n 2))

(ns my.other.namespace)
(my.little.namespace/plus2 7)
; => 9
```

To save some typing, you can `require` a namespace and give it a shorter local name

```clojure
(ns my.other.namespace
  (require [my.little.namespace :as mln]))
(mln/plus2 7)
```

[A more detailed namespace tutorial](http://blog.8thlight.com/colin-jones/2010/12/05/clojure-libs-and-namespaces-require-use-import-and-ns.html)

## Atoms

While the core datastructures are immutable, sometimes we do still want to deal with a value that changes over time. Clojure provides a bunch of nice abstractions for dealing with mutable state, we'll just look at the simplest one - an `atom`.

An **atom** is a reference type that wraps and immutable value, the value can be extracted or updated at any time. The `deref` function is used to extract the current value.

```clojure
(def foo (atom 1))
; => #<Atom: 1>

(deref foo)
; => 1
; this is common, so there's a shorthand
@foo
; => 1
```

The value can either be changed wholesale via `reset!`, or modified with a function using `swap!`.

```clojure
(reset! foo 2)
; => 2
@foo
; => 2

(swap! foo inc)
; => 3
@foo
; => 3

(swap! foo + 7)
; => 10
```

Atoms are discussed further in the [Clojure Documentation](http://clojure.org/atoms)

## JavaScript

One of the core features of Clojure and ClojureScript is that they maintain a close relation with their host platforms - the JVM and JavaScript.

JavaScript globals can be accessed via the `js/` namespace. Method calls are functions beginning with a dot (the object becomes argument 1), property access by prefixing with a dot and dash. `set!` can be used to modify JS values.

```clojure
(js/alert "Woooot")
; alert box!

(.getElementById js/document "main")
; => #<[object HTMLDivElement]>

(.-navigator js/window)
; => #<[object Navigator]>

(.. js/window -navigator -platform)
; => "MacIntel"

(set! (.-foo js/window) "bar")
; => "bar"
(.-foo js/window)
; => "bar"
```

## That'll do for now

That should probably give you enough to get along, the links below have more stuff and more detail.

## Other Links

* [Cheatsheet](http://jafingerhut.github.io/cheatsheet/clojuredocs/cheatsheet-tiptip-cdocs-summary.html) - Very quick reference for functions with examples
* [Clojure Distilled](http://yogthos.github.io/ClojureDistilled.html) - A good overview of the language, a bit longer than this one
* [Official Documentation](http://clojure.org/documentation) - Detailed dicussions of what is available and why it works that way
* [Clojure from the ground up](http://aphyr.com/tags/Clojure-from-the-ground-up) - A great set of introduction tutorials
