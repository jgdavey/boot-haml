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


(core/deftask haml
  "Compile haml to html."
  [u ugly     bool "Enables haml's \"ugly\" (non-indented) output mode."]
  (let [tmp (core/temp-dir!)
        jrb (make-jruby)
        renderer (-> "haml_renderer.rb" io/resource slurp)
        filename #(.getName (tmpfile %))]
    (core/with-pre-wrap fileset
      (let [haml-files (->> fileset core/input-files (core/by-ext [".haml"])) ]
        (jrb fileset
           :gem [["haml" "4.0.5"]]
           :set-var {"$input" (mapv #(.getAbsolutePath (tmpfile %)) haml-files)
                     "$outdir" (.getPath tmp)
                     "$ugly" ugly}
           :eval [renderer])
        (println
          (str "<< Compiled " (apply str (interpose ", " (map filename haml-files))) " >>\n\n"))
        (-> fileset
            (core/rm haml-files)
            (core/add-resource tmp)
            core/commit!)))))
