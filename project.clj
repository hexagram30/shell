(defproject hexagram30/shell "0.1.0-SNAPSHOT"
  :description "A user shell for hexagramMUSH"
  :url "https://github.com/hexagram30/shell"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [
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
    :test {
      :plugins [[lein-ltest "0.3.0"]]}}
  :aliases {
    "ltest"
      ["with-profile" "+test" "ltest"]})
