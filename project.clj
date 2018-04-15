(defn get-banner
  []
  (try
    (str
      (slurp "resources/text/banner.txt")
      (slurp "resources/text/loading.txt"))
    ;; If another project can't find the banner, just skip it;
    ;; this function is really only meant to be used by Dragon itself.
    (catch Exception _ "")))

(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m λ\u001B[m=> "))

(defproject hexagram30/shell "0.1.0-SNAPSHOT"
  :description "Various shell implementations for use by hexagram30 projects"
  :url "https://github.com/hexagram30/shell"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
    [clojusc/twig "0.3.2"]
    [clojusc/dev-system "0.1.0"]
    [hexagram30/common "0.1.0-SNAPSHOT"]
    [org.clojure/clojure "1.8.0"]]
  :plugins [
    [venantius/ultra "0.5.2"]]
  :ultra {
    :repl {
      :width 180
      :map-delimiter ""
      :extend-notation true
      :print-meta true}}
  :profiles {
    :ubercompile {
      :aot :all}
    :dev {
      :dependencies [
        [clojusc/trifl "0.2.0"]
        [org.clojure/tools.namespace "0.2.11"]]
      :source-paths ["dev-resources/src"]
      :repl-options {
        :init-ns hxgm30.shell.dev
        :prompt ~get-prompt
        :init ~(println (get-banner))}}
    :lint {
      :source-paths ^:replace ["src"]
      :test-paths ^:replace []
      :plugins [
        [jonase/eastwood "0.2.5"]
        [lein-ancient "0.6.15"]
        [lein-bikeshed "0.5.1"]
        [lein-kibit "0.1.6"]
        [venantius/yagni "0.1.4"]]}
    :test {
      :plugins [[lein-ltest "0.3.0"]]}}
  :aliases {
    "ubercompile" ["do"
      ["clean"]
      ["with-profile" "+ubercompile" "compile"]]
    "check-vers" ["with-profile" "+lint" "ancient" "check" ":all"]
    "check-jars" ["with-profile" "+lint" "do"
      ["deps" ":tree"]
      ["deps" ":plugin-tree"]]
    "check-deps" ["do"
      ["check-jars"]
      ["check-vers"]]
    "kibit" ["with-profile" "+lint" "kibit"]
    "eastwood" ["with-profile" "+lint" "eastwood" "{:namespaces [:source-paths]}"]
    "lint" ["do"
      ["kibit"]
      ;["eastwood"]
      ]
    "ltest"
      ["with-profile" "+test" "ltest"]})
