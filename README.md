# Boot + HAML

Builds `.haml` files to `.html`.

## Setup

In your `build.boot`:

``` clojure
(set-env!
 :resource-paths #{"resources"}
 :dependencies '[[org.clojure/clojure "1.6.0" :scope "provided"]
                 [com.joshuadavey/boot-haml "0.0.1-SNAPSHOT" :scope "test"]])

(require
  '[com.joshuadavey.boot-haml :refer [haml]])
```

## Usage

```
boot haml
```

Or, for continuous builds:

```
boot watch haml
```
