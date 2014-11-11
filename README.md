# (def shef 12)

A hopefully self-guided workshop to get to grips with ClojureScript.

We'll be building an implementation of [TodoMVC](https://github.com/tastejs/todomvc/blob/master/app-spec.md) using [figwheel](https://github.com/bhauman/lein-figwheel) and [Reagent](http://holmsand.github.io/reagent/).

## Requirements

* Leiningen
* Java JRE 7+

## Setup

This branch (master) is the finished product, to get the half-completed version of the app that you can work on, you should clone the "template" branch.

```sh
git clone https://github.com/defshef/defshef12.git -b template
cd defshef12
```

## Steps

Feel free to skip ahead or do these in a different order.

 1. Familiarise yourself with some [basic ClojureScript syntax](ClojureScript.md).
 2. Run the [figwheel server](#save-to-reload) and load the [app](http://localhost:3449).
 3. Use the `sepl.cljs` file to run some ClojureScript
 4. Try calling the functions ending in `!` to modify the application's data and watch the UI update.
 5. Using the TodoMVC site and `reference/index.html` as your reference, add the appropriate `class` and `id` attributes to style the todo list
 6. Consult the [TodoMVC Spec](https://github.com/tastejs/todomvc/blob/master/app-spec.md) if you need more info on how the following features should work.
 7. Add the **delete** button to each todo item and wire it up to the `remove-todo!` function.
 8. Add the **active** filter option to the footer, and have it update the displayed todo items.
 9. Add the **Clear Completed** button to the footer, including the item count and hiding when there are no completed todos. You can wire it up to the `clear-completed-todos!` function, or write your own.
 10. Add the **toggle-all** checkbox to the top of the list, including having it update itself when all items are ticked. You can wire it up to the `set-all-todos!` function, or write your own.

## Usage

### Save to Reload

First run the figwheel server, which also includes an nREPL server.

```sh
lein trampoline figwheel
```

You can can now access http://localhost:3449 to interact with the app.

Whenever a file is saved, the app code will automatically reload without destroying the current application state.

You can use the `sepl.cljs` file to execute bits of ClojureScript code without editing the rest of the application files.

### Terminal REPL

If you'd like a more traditional commandline REPL to interact with the application you can attach to the nREPL server from a second terminal session.

```sh
# Assuming figwheel server is running
lein trampoline repl :connect
```


You will now be at a clojure prompt

Run the following and ignore the warnings, they're not a problem

```clojure
(browser-repl)
```

Reload the app in a web browser to connect

You can now type ClojureScript here that will eval in the browser

```clojure
(js/alert "I'm in ur browser")
```

## How it works

* The figwheel server uses cljsbuild to compiled the ClojureScript into JavaScript
* The figwheel server serves up HTML/CSS & the compiled JavaScript
* `app.cljs` defines the application data using `(defonce)`
* `app.cljs` defines the application behaviour
* The figwheel server also serves up a websocket
* `main.cljs` uses Reagent to render the application with the `(render)` function
* `main.cljs` also connects to the websocket server and wires things together
* Figwheel watches the filesystem for changes
* When files change, figwheel recompiles and notifies connected clients over the websocket
* The client code re-requests the latest code from the server when it receives a change notification
* The new code overwrites the old definitions, except when `(defonce)` was used
* The client code is configured to run the `(render)` function whenever there has been a code change
