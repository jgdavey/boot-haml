(set-env!
  :source-paths   #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure       "1.6.0"     :scope "provided"]
                  [boot/core                 "2.0.0-rc8" :scope "provided"]
                  [boot-jruby                "0.3.0"]
                  [adzerk/bootlaces          "0.1.9"     :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.0.1")

(bootlaces! +version+)

(task-options!
 pom  {:project     'com.joshuadavey/boot-haml
       :version     +version+
       :description "Boot task to compile haml to html"
       :url         "https://github.com/jgdavey/boot-haml"
       :scm         {:url "https://github.com/jgdavey/boot-haml"}
       :license     {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask show-files
  "A debug task to println any files with :ext in the fileset"
  [e ext PATH [str] "extensions to show"]
  (with-pre-wrap fileset
    (doseq [:let [out-f (->> [user-files input-files output-files]
                          (mapcat #(% fileset))
                          set)]
            f (if (seq ext) (by-ext ext out-f) out-f)]
      (println (str "\n" (tmppath f) ":\n\n")
        (slurp (tmpfile f))))
    fileset))
