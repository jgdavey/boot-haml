(ns com.joshuadavey.boot-haml
  {:boot/export-tasks true}
  (:require
    [clojure.java.io  :as io]
    [clojure.pprint   :as pp]
    [boot.pod         :as pod]
    [boot.core        :as core :refer [tmpfile]]
    [boot.file        :as file]
    [boot.util        :refer [info dbug]]
    [boot.jruby       :refer [jruby make-jruby]]))

(def layout-names ["layout.html.haml" "layout.haml"])

(defn- only-one [coll]
  (if (< 1 (count coll))
    (throw (Exception. (str "Expected only one, found " (pr-str coll))))
    (first coll)))

(defn- filename [f]
  (.getName (tmpfile f)))

(def default-layout
  (io/resource "default_layout.haml"))

(defn layout [fileset]
  (let [candidates (->> fileset core/input-files (core/by-name layout-names))
        user-layout (only-one candidates)]
    (slurp (if user-layout
             (tmpfile user-layout)
             default-layout))))

(core/deftask haml
  "Compile haml to html."
  [u ugly     bool "Enables haml's \"ugly\" (non-indented) output mode."]
  (let [renderer (-> "haml_renderer.rb" io/resource slurp)
        jrb (make-jruby)]
    (core/with-pre-wrap fileset
      (let [all-haml-files (->> fileset core/input-files (core/by-ext [".haml"]))
            haml-files (core/not-by-name layout-names all-haml-files)
            layout' (layout fileset)]
        (if (seq haml-files)
          (do
            (println
              (str "<< Compiling "
                   (apply str (interpose ", " (map filename haml-files))) " >>\n\n"))
            (-> (jrb fileset
                     :gem [["haml" "4.0.5"]]
                     :set-var {"$input"  (mapv #(.getAbsolutePath (tmpfile %)) haml-files)
                               "$ugly"   ugly
                               "$layout" layout'}
                     :eval [renderer])
                (core/rm all-haml-files)
                core/commit!))
          fileset)))))
