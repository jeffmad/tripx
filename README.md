# tripx
Instead of creating a graph of objects and mapping and transforming them to other objects, this approach defines a series of steps to start with the source data, transform it, and add it to a growing map which serves as the result.  Each step clearly defines how each element is processed, and intermediate results can also be stored in the state. It is also easier to divide up the tasks among the team. 

First, In order to build a JSON response, define a multimethod  called "take-step".  
```clojure
(defmulti take-step (fn [state {:keys [step]}] step))
```
Create a declarative list of steps. 
```clojure
(defn build-movie-steps [req]
  [{:step :read-movie-details
    :movie-id (get-in req [:movieid])
    :locale (get-in req [:locale])
    :host (get-in req [:host])}
   {:step :parse-movie}
   {:step :add-movie-ratings}
   {:step :add-plot-summary}])
```
Build out all the steps. assoc the result of the step into the state
```clojure
(defmethod take-step :movie-release-date
  [state step]
  (assoc-in state [:property :release-date]
            (-> (p/movie-release-date (:z state))
                t/date-from-str
                t/time-map)))
```
Now use reduce to execute all the steps and collect them into a map that can be converted to JSON
```clojure
(defn execute [steps]
  (let [result (reduce take-step {:status :ok} steps)
        {:keys [status]} result]
    (when (not= status :ok)
      (println "omg!"))
    result))
```
Alternatively, use reductions to see what happens in every step for debugging
```clojure
(defn execute-debug [steps]
  (let [result (reductions take-step {:status :ok} steps)
        {:keys [trip]} result]
    (println trip)))
```
Run it with a circuit breaker so it stops running steps if there is an error
```clojure
(defn run-with-circuit-breaker
  ([z] z)
  ([r v]
   (let [s (take-step r v)]
     (if (= :ok (get s :status))
       s
       (reduced s)))))

(defn execute-cb [steps]
  (let [result (reduce run-with-circuit-breaker
                       {:status :ok}
                       steps)
        {:keys [status]} result]
    (when (not= status :ok)
      (println "omg!"))
    result))
```
Use transducer
```clojure
(defn stop-if-error [v]
  (if (= :ok (get v :status))
    v
    (reduced v)))
    
(defn execute-xf [steps]
  (let [result (reduce (comp stop-if-error take-step)
                       {:status :ok}
                       steps)
        {:keys [status]} result]
    (when (not= status :ok)
      (println "omg!"))
    result))
```

## Developing

### Setup

When you first clone this repository, run:

```sh
lein setup
```

This will create files for local configuration, and prep your system
for the project.

### Environment

To begin developing, start with a REPL.

```sh
lein repl
```

Run `go` to initiate and start the system.

```clojure
user=> (go)
:started
```

By default this creates a web server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
user=> (reset)
:reloading (...)
:resumed
```

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```clojure
user=> (test)
...
```

But you can also run tests through Leiningen.

```sh
lein test
```

### Generators

This project has several [generators][] to help you create files.

* `lein gen endpoint <name>` to create a new endpoint
* `lein gen component <name>` to create a new component
* `lein generate namespace bar.core` to create `src/bar/core.clj` and `test/bar/core_test.clj`


## Deploying

FIXME: steps to deploy

## Legal

Copyright © 2015 FIXME
