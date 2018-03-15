(ns hxgm30.shell.dev
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as repl]
    [clojusc.twig :as logger]
    [hxgm30.shell.core :as shell]
    [hxgm30.shell.evaluator.core :as evaluator]
    [hxgm30.shell.reader.grammar :as grammar]
    [hxgm30.shell.reader.parser :as parser]))

(logger/set-level! ['hxgm30] :info)

(def refresh #'repl/refresh)
